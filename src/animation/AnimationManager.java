package animation;

import animation.customComponents.AnimatedJComponent;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.Dimension;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The animation manager
 * @since 1.0.0
 * @author Yukurotei
 */
public class AnimationManager {

    /**
     * The different types of easing
     * @since 1.0.0
     * @author Yukurotei
     * @see <a href="https://easings.net">easings</a>
     */
    public enum Easing {
        LINEAR,
        EASE_IN_QUAD,
        EASE_OUT_QUAD,
        EASE_IN_OUT_QUAD,
        EASE_IN_CUBIC,
        EASE_OUT_CUBIC,
        EASE_IN_OUT_CUBIC,
        EASE_IN_SINE,
        EASE_OUT_SINE,
        EASE_IN_OUT_SINE,
        EASE_IN_EXPO,
        EASE_OUT_EXPO,
        EASE_IN_OUT_EXPO,
        EASE_IN_QUART,
        EASE_OUT_QUART,
        EASE_IN_OUT_QUART,
        EASE_IN_QUINT,
        EASE_OUT_QUINT,
        EASE_IN_OUT_QUINT,
        EASE_IN_CIRC,
        EASE_OUT_CIRC,
        EASE_IN_OUT_CIRC,
        EASE_IN_BACK,
        EASE_OUT_BACK,
        EASE_IN_OUT_BACK,
        EASE_IN_ELASTIC,
        EASE_OUT_ELASTIC,
        EASE_IN_OUT_ELASTIC,
        EASE_IN_BOUNCE,
        EASE_OUT_BOUNCE,
        EASE_IN_OUT_BOUNCE,
        EASE_OSCILLATE_1,
        EASE_OSCILLATE_3,
        EASE_OSCILLATE_5,
        EASE_OSCILLATE_INFINITE
    }

    private final ConcurrentLinkedQueue<Animation> animations = new ConcurrentLinkedQueue<>();
    private final EventManager eventManager;
    private final Timer timer;
    private long lastUpdate;
    private float currentTotalTime = 0;

    // Default constructor
    /**
     * Creates an animation manager that updates every 16ms (aprox 60fps)
     * @since 1.0.0
     * @author Yukurotei
     */
    public AnimationManager() {
        this(16); // Default update interval of 16 milliseconds (approx 60 FPS)
    }

    // Constructor with custom update interval
    /**
     * Creates an animation manager that updates at the rate that is specified
     * @param updateInterval - The rate in ms at which the manager updates (shorter = smoother BUT = more resources used)
     * @since 1.0.0
     * @author Yukurotei
     */
    public AnimationManager(int updateInterval) {
        this.eventManager = new EventManager();
        lastUpdate = System.nanoTime();
        timer = new Timer(updateInterval, e -> update());
        timer.start();
    }
    /**
     * Restarts the animation manager
     * @since 1.0.0
     * @author MEME-KING16
     */
    public void start() {
        timer.start();
    }
    /**
     * Stops the animation manager
     * @since 1.0.0
     * @author Yukurotei
     */
    public void stop() {
        timer.stop();
    }

    private void update() {
        long now = System.nanoTime();
        float delta = (now - lastUpdate) / 1_000_000_000.0f;
        lastUpdate = now;
        currentTotalTime += delta;

        List<Animation> currentAnimations = new ArrayList<>(animations);

        //group anims together by component (I LOVE HASH MAP)
        Map<JComponent, List<Animation>> animationsByTarget = new HashMap<>();
        for (Animation anim : currentAnimations) {
            if (!anim.isFinished()) {
                animationsByTarget.computeIfAbsent(anim.getTarget(), k -> new ArrayList<>()).add(anim);
            }
        }

        //iterate through and apply all anims at the same time to resolve conflicts
        for (Map.Entry<JComponent, List<Animation>> entry : animationsByTarget.entrySet()) {
            JComponent target = entry.getKey();
            List<Animation> targetAnims = entry.getValue();

            // Update all animations and collect their results
            Float finalX = null, finalY = null;
            Integer finalWidth = null, finalHeight = null;
            Float finalOpacity = null;
            Float finalRotation = null;

            for (Animation anim : targetAnims) {
                anim.updateInternal(delta);

                AnimationState state = anim.getCurrentState();
                if (state.hasPosition) {
                    finalX = state.x;
                    finalY = state.y;
                }
                if (state.hasSize) {
                    finalWidth = state.width;
                    finalHeight = state.height;
                }
                if (state.hasOpacity) {
                    finalOpacity = state.opacity;
                }
                if (state.hasRotation) {
                    finalRotation = state.rotation;
                }
            }

            //apply all change
            if (finalWidth != null) {
                if (finalX != null) {
                    target.setBounds(finalX.intValue(), finalY.intValue(), finalWidth, finalHeight);
                } else {
                    target.setSize(finalWidth, finalHeight);
                }
            } else if (finalX != null) {
                target.setLocation(finalX.intValue(), finalY.intValue());
            }

            if (finalOpacity != null) {
                target.putClientProperty("animation.opacity", finalOpacity);
                target.repaint();
            }

            if (finalRotation != null) {
                target.putClientProperty("animation.rotation", finalRotation);
                if (target.getParent() != null) {
                    target.getParent().repaint();
                } else {
                    target.repaint();
                }
            }
        }

        currentAnimations.stream().filter(Animation::isFinished).forEach(animations::remove);

        eventManager.update(currentTotalTime);
    }

    /**
     * Move a JComponent with an easing
     * @param target - The JComponent
     * @param toX - The new x
     * @param toY - The new y
     * @param duration - The duration of the animation (in seconds)
     * @param easing - The easing
     * @since 1.0.0
     * @author Yukurotei
     */
    public void animateMove(JComponent target, float toX, float toY, float duration, Easing easing) {
        Animation anim = new Animation();
        anim.initMove(target, toX, toY, duration, easing);
        animations.add(anim);
    }

    /**
     * Fade an AnimatedJComponent with an easing
     * @param target - The AnimatedJComponent
     * @param toOpacity - The new opacity (0-1)
     * @param duration - The duration of the animation (in seconds)
     * @param easing - The easing
     * @since 1.0.0
     * @author Yukurotei
     */
    public void animateFade(AnimatedJComponent target, float toOpacity, float duration, Easing easing) {
        //Implemented with AlphaComposite
        if (target == null) {
            throw new IllegalArgumentException("target can not be null");
        }
        Animation anim = new Animation();
        anim.initFade((JComponent) target, toOpacity, duration, easing);
        animations.add(anim);
    }

    /**
     * Change the scale of a JComponent with an easing
     * @param target - The JComponent
     * @param toScaleXPercentage - The new x scale in DECIMAL PERCENTAGE (1 is 100%, 0.1 is 10%, so on)
     * @param toScaleYPercentage - The new y scale in DECIMAL PERCENTAGE (1 is 100%, 0.1 is 10%, so on)
     * @param duration - The duration of the animation (in seconds)
     * @param easing - The easing
     * @since 1.0.0
     * @author Yukurotei
     */
    public void animateScale(JComponent target, float toScaleXPercentage, float toScaleYPercentage, float duration, Easing easing) {
        Animation anim = new Animation();
        anim.initScale(target, toScaleXPercentage, toScaleYPercentage, duration, easing);
        animations.add(anim);
    }

    /**
     * Rotate the AnimatedJComponent with an easing
     * @param target - The AnimatedJComponent
     * @param toRotationDegrees - The amount of degrees to rotate
     * @param duration - The duration of the animation (in seconds)
     * @param easing - The easing
     * @since 1.0.0
     * @author Yukurotei
     */
    public void animateRotation(AnimatedJComponent target, float toRotationDegrees, float duration, Easing easing) {
        //Graphics2D.rotate(), manual paint
        if (target == null) {
            throw new IllegalArgumentException("target can not be null");
        }
        Animation anim = new Animation();
        anim.initRotation((JComponent) target, toRotationDegrees, duration, easing);
        animations.add(anim);
    }

    /**
     * Get the Event Manager
     * @return EventManager
     * @since 1.0.0
     * @author Yukurotei
     */
    public EventManager getEventManager() {
        return eventManager;
    }

    private static class AnimationState {
        boolean hasPosition = false;
        float x, y;

        boolean hasSize = false;
        int width, height;

        boolean hasOpacity = false;
        float opacity;

        boolean hasRotation = false;
        float rotation;
    }

    private static class Animation {
        private enum AnimationType {
            MOVE, SCALE, FADE, ROTATION
        }

        private JComponent target;
        private AnimationType type;
        private Easing easing;
        private float duration;
        private float time;

        //Position
        private float startX, startY;
        private float toX, toY;

        //Scale
        private Dimension startSize;
        private float startCenterX, startCenterY; // Store center point for scale
        private float toScaleX, toScaleY;

        //Opacity
        private float startOpacity;
        private float toOpacity;

        //Rotation
        private float startRotation;
        private float toRotation;

        public JComponent getTarget() {
            return target;
        }

        public void initMove(JComponent target, float toX, float toY, float duration, Easing easing) {
            this.target = target;
            this.type = AnimationType.MOVE;
            this.duration = duration;
            this.easing = easing;
            this.time = 0;

            this.startX = target.getX();
            this.startY = target.getY();
            this.toX = toX;
            this.toY = toY;
        }

        public void initScale(JComponent target, float toScaleX, float toScaleY, float duration, Easing easing) {
            this.target = target;
            this.type = AnimationType.SCALE;
            this.duration = duration;
            this.easing = easing;
            this.time = 0;

            this.startSize = target.getSize();
            this.startCenterX = target.getX() + target.getWidth() / 2.0f;
            this.startCenterY = target.getY() + target.getHeight() / 2.0f;
            this.toScaleX = toScaleX;
            this.toScaleY = toScaleY;
        }

        public void initFade(JComponent target, float toOpacity, float duration, Easing easing) {
            this.target = target;
            this.type = AnimationType.FADE;
            this.duration = duration;
            this.easing = easing;
            this.time = 0;

            Object prop = target.getClientProperty("animation.opacity");
            this.startOpacity = (prop instanceof Float) ? (Float) prop : 1f;
            this.toOpacity = toOpacity;
        }

        public void initRotation(JComponent target, float toRotationDegrees, float duration, Easing easing) {
            this.target = target;
            this.type = AnimationType.ROTATION;
            this.duration = duration;
            this.easing = easing;
            this.time = 0;

            Object prop = target.getClientProperty("animation.rotation");
            this.startRotation = (prop instanceof Float) ? (Float) prop : 0f;
            this.toRotation = (float) Math.toRadians(toRotationDegrees);

            this.startCenterX = target.getX() + target.getWidth() / 2.0f;
            this.startCenterY = target.getY() + target.getHeight() / 2.0f;
            this.startSize = target.getSize();
        }

        public void updateInternal(float delta) {
            if (isFinished()) return;

            time += delta;
            if (time > duration) time = duration;
        }

        public AnimationState getCurrentState() {
            AnimationState state = new AnimationState();

            float progress = Math.min(1f, time / duration);
            float easedProgress = applyEasing(progress);

            switch (type) {
                case MOVE:
                    state.hasPosition = true;
                    state.x = startX + (toX - startX) * easedProgress;
                    state.y = startY + (toY - startY) * easedProgress;
                    break;

                case SCALE:
                    float currentScaleX = 1.0f + (toScaleX - 1.0f) * easedProgress;
                    float currentScaleY = 1.0f + (toScaleY - 1.0f) * easedProgress;

                    int newWidth = (int) (startSize.width * currentScaleX);
                    int newHeight = (int) (startSize.height * currentScaleY);

                    float newX = startCenterX - newWidth / 2.0f;
                    float newY = startCenterY - newHeight / 2.0f;

                    state.hasPosition = true;
                    state.x = newX;
                    state.y = newY;
                    state.hasSize = true;
                    state.width = newWidth;
                    state.height = newHeight;
                    break;

                case FADE:
                    state.hasOpacity = true;
                    state.opacity = startOpacity + (toOpacity - startOpacity) * easedProgress;
                    break;

                case ROTATION:
                    state.hasRotation = true;
                    state.rotation = startRotation + (toRotation - startRotation) * easedProgress;

                    state.hasPosition = true;
                    state.x = startCenterX - startSize.width / 2.0f;
                    state.y = startCenterY - startSize.height / 2.0f;
                    break;
            }

            return state;
        }

        public boolean isFinished() {
            return time >= duration;
        }

        private float applyEasing(float t) {
            switch (easing) {
                case EASE_IN_QUAD:
                    return t * t;
                case EASE_OUT_QUAD:
                    return t * (2 - t);
                case EASE_IN_OUT_QUAD:
                    return t < 0.5f ? 2 * t * t : -1 + (4 - 2 * t) * t;
                case EASE_IN_CUBIC:
                    return t * t * t;
                case EASE_OUT_CUBIC:
                    return 1 - (float) Math.pow(1 - t, 3);
                case EASE_IN_OUT_CUBIC:
                    return t < 0.5f ? 4 * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 3) / 2;
                case EASE_IN_SINE:
                    return 1 - (float) Math.cos((t * Math.PI) / 2);
                case EASE_OUT_SINE:
                    return (float) Math.sin((t * Math.PI) / 2);
                case EASE_IN_OUT_SINE:
                    return -((float) Math.cos(Math.PI * t) - 1) / 2;
                case EASE_IN_EXPO:
                    return t == 0 ? 0 : (float) Math.pow(2, 10 * t - 10);
                case EASE_OUT_EXPO:
                    return t == 1 ? 1 : 1 - (float) Math.pow(2, -10 * t);
                case EASE_IN_OUT_EXPO:
                    if (t == 0) return 0;
                    if (t == 1) return 1;
                    if (t < 0.5f) return (float) Math.pow(2, 20 * t - 10) / 2;
                    return (2 - (float) Math.pow(2, -20 * t + 10)) / 2;
                case EASE_IN_QUART:
                    return t * t * t * t;
                case EASE_OUT_QUART:
                    return 1 - (float) Math.pow(1 - t, 4);
                case EASE_IN_OUT_QUART:
                    return t < 0.5f ? 8 * t * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 4) / 2;
                case EASE_IN_QUINT:
                    return t * t * t * t * t;
                case EASE_OUT_QUINT:
                    return 1 - (float) Math.pow(1 - t, 5);
                case EASE_IN_OUT_QUINT:
                    return t < 0.5f ? 16 * t * t * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 5) / 2;
                case EASE_IN_CIRC:
                    return 1 - (float) Math.sqrt(1 - t * t);
                case EASE_OUT_CIRC:
                    return (float) Math.sqrt(1 - (float) Math.pow(t - 1, 2));
                case EASE_IN_OUT_CIRC:
                    return t < 0.5f ? (1 - (float) Math.sqrt(1 - (float) Math.pow(2 * t, 2))) / 2
                            : ((float) Math.sqrt(1 - (float) Math.pow(-2 * t + 2, 2)) + 1) / 2;
                case EASE_IN_BACK: {
                    final float c1 = 1.70158f;
                    final float c3 = c1 + 1f;
                    return c3 * t * t * t - c1 * t * t;
                }
                case EASE_OUT_BACK: {
                    final float c1 = 1.70158f;
                    final float c3 = c1 + 1f;
                    return 1 + c3 * (float) Math.pow(t - 1, 3) + c1 * (float) Math.pow(t - 1, 2);
                }
                case EASE_IN_OUT_BACK: {
                    final float c1 = 1.70158f;
                    final float c2 = c1 * 1.525f;
                    return t < 0.5f
                            ? ((float) Math.pow(2 * t, 2) * ((c2 + 1) * 2 * t - c2)) / 2
                            : ((float) Math.pow(2 * t - 2, 2) * ((c2 + 1) * (2 * t - 2) + c2) + 2) / 2;
                }
                case EASE_IN_ELASTIC: {
                    final float c4 = (2 * (float) Math.PI) / 3;
                    if (t == 0) return 0;
                    if (t == 1) return 1;
                    return -(float) Math.pow(2, 10 * t - 10) * (float) Math.sin((t * 10 - 10.75) * c4);
                }
                case EASE_OUT_ELASTIC: {
                    final float c4 = (2 * (float) Math.PI) / 3;
                    if (t == 0) return 0;
                    if (t == 1) return 1;
                    return (float) Math.pow(2, -10 * t) * (float) Math.sin((t * 10 - 0.75) * c4) + 1;
                }
                case EASE_IN_OUT_ELASTIC: {
                    final float c5 = (2 * (float) Math.PI) / 4.5f;
                    if (t == 0) return 0;
                    if (t == 1) return 1;
                    if (t < 0.5f) {
                        return -((float) Math.pow(2, 20 * t - 10) * (float) Math.sin((20 * t - 11.125) * c5)) / 2;
                    }
                    return ((float) Math.pow(2, -20 * t + 10) * (float) Math.sin((20 * t - 11.125) * c5)) / 2 + 1;
                }
                case EASE_IN_BOUNCE:
                    return 1 - easeOutBounce(1 - t);
                case EASE_OUT_BOUNCE:
                    return easeOutBounce(t);
                case EASE_IN_OUT_BOUNCE:
                    return t < 0.5f ? (1 - easeOutBounce(1 - 2 * t)) / 2 : (1 + easeOutBounce(2 * t - 1)) / 2;
                case EASE_OSCILLATE_1:
                    return (1 - (float) Math.cos(t * 2 * Math.PI)) / 2;
                case EASE_OSCILLATE_3:
                    return (1 - (float) Math.cos(t * 3 * 2 * Math.PI)) / 2;
                case EASE_OSCILLATE_5:
                    return (1 - (float) Math.cos(t * 5 * 2 * Math.PI)) / 2;
                case EASE_OSCILLATE_INFINITE:
                    return (1 - (float) Math.cos(t * 9999 * 2 * Math.PI)) / 2;
                case LINEAR:
                default:
                    return t;
            }
        }

        private float easeOutBounce(float t) {
            final float n1 = 7.5625f;
            final float d1 = 2.75f;

            if (t < 1f / d1) {
                return n1 * t * t;
            } else if (t < 2f / d1) {
                t -= 1.5f / d1;
                return n1 * t * t + 0.75f;
            } else if (t < 2.5f / d1) {
                t -= 2.25f / d1;
                return n1 * t * t + 0.9375f;
            } else {
                t -= 2.625f / d1;
                return n1 * t * t + 0.984375f;
            }
        }
    }
}