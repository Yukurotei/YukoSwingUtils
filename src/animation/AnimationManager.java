package animation;

import animation.customComponents.AnimatedJComponent;

import javax.swing.*;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
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

        for (Animation anim : currentAnimations) {
            if (anim.isFinished()) {
                animations.remove(anim);
            } else {
                anim.update(delta);
            }
        }
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

    private static class Animation {
        private JComponent target;
        private Easing easing;
        private float duration;
        private float time;

        //Position
        private float startX, startY;
        private float toX, toY;

        //Scale
        private Dimension startSize;
        private float toScaleX, toScaleY;

        //Opacity
        private float startOpacity;
        private float toOpacity;

        //Rotation
        private float startRotation;
        private float toRotation;


        public void initMove(JComponent target, float toX, float toY, float duration, Easing easing) {
            this.target = target;
            this.duration = duration;
            this.easing = easing;
            this.time = 0;

            this.startX = target.getX();
            this.startY = target.getY();
            this.toX = toX;
            this.toY = toY;

            this.toScaleX = Float.NaN;
            this.toScaleY = Float.NaN;
            this.toOpacity = Float.NaN;
            this.toRotation = Float.NaN;
        }

        public void initScale(JComponent target, float toScaleX, float toScaleY, float duration, Easing easing) {
            this.target = target;
            this.duration = duration;
            this.easing = easing;
            this.time = 0;

            this.startX = target.getX();
            this.startY = target.getY();
            this.startSize = target.getSize();
            this.toScaleX = toScaleX;
            this.toScaleY = toScaleY;

            this.toX = Float.NaN;
            this.toY = Float.NaN;
            this.toOpacity = Float.NaN;
            this.toRotation = Float.NaN;
        }

        public void initFade(JComponent target, float toOpacity, float duration, Easing easing) {
            this.target = target;
            this.duration = duration;
            this.easing = easing;
            this.time = 0;

            Object prop = target.getClientProperty("animation.opacity");
            this.startOpacity = (prop instanceof Float) ? (Float) prop : 1f;
            this.toOpacity = toOpacity;

            this.toX = Float.NaN;
            this.toY = Float.NaN;
            this.toScaleX = Float.NaN;
            this.toScaleY = Float.NaN;
            this.toRotation = Float.NaN;
        }

        public void initRotation(JComponent target, float toRotationDegrees, float duration, Easing easing) {
            this.target = target;
            this.duration = duration;
            this.easing = easing;
            this.time = 0;

            Object prop = target.getClientProperty("animation.rotation");
            this.startRotation = (prop instanceof Float) ? (Float) prop : 0f;
            this.toRotation = (float) Math.toRadians(toRotationDegrees);

            this.toX = Float.NaN;
            this.toY = Float.NaN;
            this.toScaleX = Float.NaN;
            this.toScaleY = Float.NaN;
            this.toOpacity = Float.NaN;
        }

        public void update(float delta) {
            if (isFinished()) return;

            time += delta;
            float progress = Math.min(1f, time / duration);
            float easedProgress = applyEasing(progress);

            if (!Float.isNaN(toX)) {
                float newX = startX + (toX - startX) * easedProgress;
                target.setLocation((int) newX, target.getY());
            }
            if (!Float.isNaN(toY)) {
                float newY = startY + (toY - startY) * easedProgress;
                target.setLocation(target.getX(), (int) newY);
            }

            if (!Float.isNaN(toScaleX) || !Float.isNaN(toScaleY)) {
                float currentScaleX = 1.0f;
                if (!Float.isNaN(toScaleX)) {
                    currentScaleX = 1.0f + (toScaleX - 1.0f) * easedProgress;
                }
                int newWidth = (int) (startSize.width * currentScaleX);

                float currentScaleY = 1.0f;
                if (!Float.isNaN(toScaleY)) {
                    currentScaleY = 1.0f + (toScaleY - 1.0f) * easedProgress;
                }
                int newHeight = (int) (startSize.height * currentScaleY);

                //adjust position to keep the component centered
                int newX = (int) (startX + (startSize.width - newWidth) / 2.0f);
                int newY = (int) (startY + (startSize.height - newHeight) / 2.0f);

                target.setBounds(newX, newY, newWidth, newHeight);
            }

            if (!Float.isNaN(toOpacity)) {
                float newOpacity = startOpacity + (toOpacity - startOpacity) * easedProgress;
                target.putClientProperty("animation.opacity", newOpacity);
                target.repaint();
            }

            if (!Float.isNaN(toRotation)) {
                float newRotation = startRotation + (toRotation - startRotation) * easedProgress;
                target.putClientProperty("animation.rotation", newRotation);
                if (target.getParent() != null) {
                    //repaint the parent to clean up ghost pixels
                    target.getParent().repaint();
                } else {
                    target.repaint();
                }
            }

            if (progress >= 1f) {
                time = duration;
            }
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
