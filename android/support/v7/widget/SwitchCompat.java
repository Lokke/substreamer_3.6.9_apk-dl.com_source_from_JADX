package android.support.v7.widget;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.PointerIconCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.C0136R;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.text.AllCapsTransformationMethod;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.Property;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CompoundButton;
import com.ghenry22.substream2.C0173R;
import org.apache.cordova.file.FileUtils;

@TargetApi(14)
@RequiresApi(14)
public class SwitchCompat extends CompoundButton {
    private static final String ACCESSIBILITY_EVENT_CLASS_NAME = "android.widget.Switch";
    private static final int[] CHECKED_STATE_SET;
    private static final int MONOSPACE = 3;
    private static final int SANS = 1;
    private static final int SERIF = 2;
    private static final int THUMB_ANIMATION_DURATION = 250;
    private static final Property<SwitchCompat, Float> THUMB_POS;
    private static final int TOUCH_MODE_DOWN = 1;
    private static final int TOUCH_MODE_DRAGGING = 2;
    private static final int TOUCH_MODE_IDLE = 0;
    private boolean mHasThumbTint;
    private boolean mHasThumbTintMode;
    private boolean mHasTrackTint;
    private boolean mHasTrackTintMode;
    private int mMinFlingVelocity;
    private Layout mOffLayout;
    private Layout mOnLayout;
    ObjectAnimator mPositionAnimator;
    private boolean mShowText;
    private boolean mSplitTrack;
    private int mSwitchBottom;
    private int mSwitchHeight;
    private int mSwitchLeft;
    private int mSwitchMinWidth;
    private int mSwitchPadding;
    private int mSwitchRight;
    private int mSwitchTop;
    private TransformationMethod mSwitchTransformationMethod;
    private int mSwitchWidth;
    private final Rect mTempRect;
    private ColorStateList mTextColors;
    private CharSequence mTextOff;
    private CharSequence mTextOn;
    private TextPaint mTextPaint;
    private Drawable mThumbDrawable;
    private float mThumbPosition;
    private int mThumbTextPadding;
    private ColorStateList mThumbTintList;
    private Mode mThumbTintMode;
    private int mThumbWidth;
    private int mTouchMode;
    private int mTouchSlop;
    private float mTouchX;
    private float mTouchY;
    private Drawable mTrackDrawable;
    private ColorStateList mTrackTintList;
    private Mode mTrackTintMode;
    private VelocityTracker mVelocityTracker;

    /* renamed from: android.support.v7.widget.SwitchCompat.1 */
    static class C01691 extends Property<SwitchCompat, Float> {
        C01691(Class x0, String x1) {
            super(x0, x1);
        }

        public Float get(SwitchCompat object) {
            return Float.valueOf(object.mThumbPosition);
        }

        public void set(SwitchCompat object, Float value) {
            object.setThumbPosition(value.floatValue());
        }
    }

    static {
        THUMB_POS = new C01691(Float.class, "thumbPos");
        int[] iArr = new int[TOUCH_MODE_DOWN];
        iArr[0] = 16842912;
        CHECKED_STATE_SET = iArr;
    }

    public SwitchCompat(Context context) {
        this(context, null);
    }

    public SwitchCompat(Context context, AttributeSet attrs) {
        this(context, attrs, C0136R.attr.switchStyle);
    }

    public SwitchCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mThumbTintList = null;
        this.mThumbTintMode = null;
        this.mHasThumbTint = false;
        this.mHasThumbTintMode = false;
        this.mTrackTintList = null;
        this.mTrackTintMode = null;
        this.mHasTrackTint = false;
        this.mHasTrackTintMode = false;
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mTempRect = new Rect();
        this.mTextPaint = new TextPaint(TOUCH_MODE_DOWN);
        Resources res = getResources();
        this.mTextPaint.density = res.getDisplayMetrics().density;
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, C0136R.styleable.SwitchCompat, defStyleAttr, 0);
        this.mThumbDrawable = a.getDrawable(C0136R.styleable.SwitchCompat_android_thumb);
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.setCallback(this);
        }
        this.mTrackDrawable = a.getDrawable(C0136R.styleable.SwitchCompat_track);
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.setCallback(this);
        }
        this.mTextOn = a.getText(C0136R.styleable.SwitchCompat_android_textOn);
        this.mTextOff = a.getText(C0136R.styleable.SwitchCompat_android_textOff);
        this.mShowText = a.getBoolean(C0136R.styleable.SwitchCompat_showText, true);
        this.mThumbTextPadding = a.getDimensionPixelSize(C0136R.styleable.SwitchCompat_thumbTextPadding, 0);
        this.mSwitchMinWidth = a.getDimensionPixelSize(C0136R.styleable.SwitchCompat_switchMinWidth, 0);
        this.mSwitchPadding = a.getDimensionPixelSize(C0136R.styleable.SwitchCompat_switchPadding, 0);
        this.mSplitTrack = a.getBoolean(C0136R.styleable.SwitchCompat_splitTrack, false);
        ColorStateList thumbTintList = a.getColorStateList(C0136R.styleable.SwitchCompat_thumbTint);
        if (thumbTintList != null) {
            this.mThumbTintList = thumbTintList;
            this.mHasThumbTint = true;
        }
        Mode thumbTintMode = DrawableUtils.parseTintMode(a.getInt(C0136R.styleable.SwitchCompat_thumbTintMode, -1), null);
        if (this.mThumbTintMode != thumbTintMode) {
            this.mThumbTintMode = thumbTintMode;
            this.mHasThumbTintMode = true;
        }
        if (this.mHasThumbTint || this.mHasThumbTintMode) {
            applyThumbTint();
        }
        ColorStateList trackTintList = a.getColorStateList(C0136R.styleable.SwitchCompat_trackTint);
        if (trackTintList != null) {
            this.mTrackTintList = trackTintList;
            this.mHasTrackTint = true;
        }
        Mode trackTintMode = DrawableUtils.parseTintMode(a.getInt(C0136R.styleable.SwitchCompat_trackTintMode, -1), null);
        if (this.mTrackTintMode != trackTintMode) {
            this.mTrackTintMode = trackTintMode;
            this.mHasTrackTintMode = true;
        }
        if (this.mHasTrackTint || this.mHasTrackTintMode) {
            applyTrackTint();
        }
        int appearance = a.getResourceId(C0136R.styleable.SwitchCompat_switchTextAppearance, 0);
        if (appearance != 0) {
            setSwitchTextAppearance(context, appearance);
        }
        a.recycle();
        ViewConfiguration config = ViewConfiguration.get(context);
        this.mTouchSlop = config.getScaledTouchSlop();
        this.mMinFlingVelocity = config.getScaledMinimumFlingVelocity();
        refreshDrawableState();
        setChecked(isChecked());
    }

    public void setSwitchTextAppearance(Context context, int resid) {
        TintTypedArray appearance = TintTypedArray.obtainStyledAttributes(context, resid, C0136R.styleable.TextAppearance);
        ColorStateList colors = appearance.getColorStateList(C0136R.styleable.TextAppearance_android_textColor);
        if (colors != null) {
            this.mTextColors = colors;
        } else {
            this.mTextColors = getTextColors();
        }
        int ts = appearance.getDimensionPixelSize(C0136R.styleable.TextAppearance_android_textSize, 0);
        if (!(ts == 0 || ((float) ts) == this.mTextPaint.getTextSize())) {
            this.mTextPaint.setTextSize((float) ts);
            requestLayout();
        }
        setSwitchTypefaceByIndex(appearance.getInt(C0136R.styleable.TextAppearance_android_typeface, -1), appearance.getInt(C0136R.styleable.TextAppearance_android_textStyle, -1));
        if (appearance.getBoolean(C0136R.styleable.TextAppearance_textAllCaps, false)) {
            this.mSwitchTransformationMethod = new AllCapsTransformationMethod(getContext());
        } else {
            this.mSwitchTransformationMethod = null;
        }
        appearance.recycle();
    }

    private void setSwitchTypefaceByIndex(int typefaceIndex, int styleIndex) {
        Typeface tf = null;
        switch (typefaceIndex) {
            case TOUCH_MODE_DOWN /*1*/:
                tf = Typeface.SANS_SERIF;
                break;
            case TOUCH_MODE_DRAGGING /*2*/:
                tf = Typeface.SERIF;
                break;
            case MONOSPACE /*3*/:
                tf = Typeface.MONOSPACE;
                break;
        }
        setSwitchTypeface(tf, styleIndex);
    }

    public void setSwitchTypeface(Typeface tf, int style) {
        boolean z = false;
        if (style > 0) {
            int typefaceStyle;
            float f;
            if (tf == null) {
                tf = Typeface.defaultFromStyle(style);
            } else {
                tf = Typeface.create(tf, style);
            }
            setSwitchTypeface(tf);
            if (tf != null) {
                typefaceStyle = tf.getStyle();
            } else {
                typefaceStyle = 0;
            }
            int need = style & (typefaceStyle ^ -1);
            TextPaint textPaint = this.mTextPaint;
            if ((need & TOUCH_MODE_DOWN) != 0) {
                z = true;
            }
            textPaint.setFakeBoldText(z);
            textPaint = this.mTextPaint;
            if ((need & TOUCH_MODE_DRAGGING) != 0) {
                f = -0.25f;
            } else {
                f = 0.0f;
            }
            textPaint.setTextSkewX(f);
            return;
        }
        this.mTextPaint.setFakeBoldText(false);
        this.mTextPaint.setTextSkewX(0.0f);
        setSwitchTypeface(tf);
    }

    public void setSwitchTypeface(Typeface tf) {
        if (this.mTextPaint.getTypeface() != tf) {
            this.mTextPaint.setTypeface(tf);
            requestLayout();
            invalidate();
        }
    }

    public void setSwitchPadding(int pixels) {
        this.mSwitchPadding = pixels;
        requestLayout();
    }

    public int getSwitchPadding() {
        return this.mSwitchPadding;
    }

    public void setSwitchMinWidth(int pixels) {
        this.mSwitchMinWidth = pixels;
        requestLayout();
    }

    public int getSwitchMinWidth() {
        return this.mSwitchMinWidth;
    }

    public void setThumbTextPadding(int pixels) {
        this.mThumbTextPadding = pixels;
        requestLayout();
    }

    public int getThumbTextPadding() {
        return this.mThumbTextPadding;
    }

    public void setTrackDrawable(Drawable track) {
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.setCallback(null);
        }
        this.mTrackDrawable = track;
        if (track != null) {
            track.setCallback(this);
        }
        requestLayout();
    }

    public void setTrackResource(int resId) {
        setTrackDrawable(AppCompatResources.getDrawable(getContext(), resId));
    }

    public Drawable getTrackDrawable() {
        return this.mTrackDrawable;
    }

    public void setTrackTintList(@Nullable ColorStateList tint) {
        this.mTrackTintList = tint;
        this.mHasTrackTint = true;
        applyTrackTint();
    }

    @Nullable
    public ColorStateList getTrackTintList() {
        return this.mTrackTintList;
    }

    public void setTrackTintMode(@Nullable Mode tintMode) {
        this.mTrackTintMode = tintMode;
        this.mHasTrackTintMode = true;
        applyTrackTint();
    }

    @Nullable
    public Mode getTrackTintMode() {
        return this.mTrackTintMode;
    }

    private void applyTrackTint() {
        if (this.mTrackDrawable == null) {
            return;
        }
        if (this.mHasTrackTint || this.mHasTrackTintMode) {
            this.mTrackDrawable = this.mTrackDrawable.mutate();
            if (this.mHasTrackTint) {
                DrawableCompat.setTintList(this.mTrackDrawable, this.mTrackTintList);
            }
            if (this.mHasTrackTintMode) {
                DrawableCompat.setTintMode(this.mTrackDrawable, this.mTrackTintMode);
            }
            if (this.mTrackDrawable.isStateful()) {
                this.mTrackDrawable.setState(getDrawableState());
            }
        }
    }

    public void setThumbDrawable(Drawable thumb) {
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.setCallback(null);
        }
        this.mThumbDrawable = thumb;
        if (thumb != null) {
            thumb.setCallback(this);
        }
        requestLayout();
    }

    public void setThumbResource(int resId) {
        setThumbDrawable(AppCompatResources.getDrawable(getContext(), resId));
    }

    public Drawable getThumbDrawable() {
        return this.mThumbDrawable;
    }

    public void setThumbTintList(@Nullable ColorStateList tint) {
        this.mThumbTintList = tint;
        this.mHasThumbTint = true;
        applyThumbTint();
    }

    @Nullable
    public ColorStateList getThumbTintList() {
        return this.mThumbTintList;
    }

    public void setThumbTintMode(@Nullable Mode tintMode) {
        this.mThumbTintMode = tintMode;
        this.mHasThumbTintMode = true;
        applyThumbTint();
    }

    @Nullable
    public Mode getThumbTintMode() {
        return this.mThumbTintMode;
    }

    private void applyThumbTint() {
        if (this.mThumbDrawable == null) {
            return;
        }
        if (this.mHasThumbTint || this.mHasThumbTintMode) {
            this.mThumbDrawable = this.mThumbDrawable.mutate();
            if (this.mHasThumbTint) {
                DrawableCompat.setTintList(this.mThumbDrawable, this.mThumbTintList);
            }
            if (this.mHasThumbTintMode) {
                DrawableCompat.setTintMode(this.mThumbDrawable, this.mThumbTintMode);
            }
            if (this.mThumbDrawable.isStateful()) {
                this.mThumbDrawable.setState(getDrawableState());
            }
        }
    }

    public void setSplitTrack(boolean splitTrack) {
        this.mSplitTrack = splitTrack;
        invalidate();
    }

    public boolean getSplitTrack() {
        return this.mSplitTrack;
    }

    public CharSequence getTextOn() {
        return this.mTextOn;
    }

    public void setTextOn(CharSequence textOn) {
        this.mTextOn = textOn;
        requestLayout();
    }

    public CharSequence getTextOff() {
        return this.mTextOff;
    }

    public void setTextOff(CharSequence textOff) {
        this.mTextOff = textOff;
        requestLayout();
    }

    public void setShowText(boolean showText) {
        if (this.mShowText != showText) {
            this.mShowText = showText;
            requestLayout();
        }
    }

    public boolean getShowText() {
        return this.mShowText;
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int thumbWidth;
        int thumbHeight;
        int maxTextWidth;
        int trackHeight;
        if (this.mShowText) {
            if (this.mOnLayout == null) {
                this.mOnLayout = makeLayout(this.mTextOn);
            }
            if (this.mOffLayout == null) {
                this.mOffLayout = makeLayout(this.mTextOff);
            }
        }
        Rect padding = this.mTempRect;
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.getPadding(padding);
            thumbWidth = (this.mThumbDrawable.getIntrinsicWidth() - padding.left) - padding.right;
            thumbHeight = this.mThumbDrawable.getIntrinsicHeight();
        } else {
            thumbWidth = 0;
            thumbHeight = 0;
        }
        if (this.mShowText) {
            maxTextWidth = Math.max(this.mOnLayout.getWidth(), this.mOffLayout.getWidth()) + (this.mThumbTextPadding * TOUCH_MODE_DRAGGING);
        } else {
            maxTextWidth = 0;
        }
        this.mThumbWidth = Math.max(maxTextWidth, thumbWidth);
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.getPadding(padding);
            trackHeight = this.mTrackDrawable.getIntrinsicHeight();
        } else {
            padding.setEmpty();
            trackHeight = 0;
        }
        int paddingLeft = padding.left;
        int paddingRight = padding.right;
        if (this.mThumbDrawable != null) {
            Rect inset = DrawableUtils.getOpticalBounds(this.mThumbDrawable);
            paddingLeft = Math.max(paddingLeft, inset.left);
            paddingRight = Math.max(paddingRight, inset.right);
        }
        int switchWidth = Math.max(this.mSwitchMinWidth, ((this.mThumbWidth * TOUCH_MODE_DRAGGING) + paddingLeft) + paddingRight);
        int switchHeight = Math.max(trackHeight, thumbHeight);
        this.mSwitchWidth = switchWidth;
        this.mSwitchHeight = switchHeight;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredHeight() < switchHeight) {
            setMeasuredDimension(getMeasuredWidthAndState(), switchHeight);
        }
    }

    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(event);
        CharSequence text = isChecked() ? this.mTextOn : this.mTextOff;
        if (text != null) {
            event.getText().add(text);
        }
    }

    private Layout makeLayout(CharSequence text) {
        CharSequence transformed;
        if (this.mSwitchTransformationMethod != null) {
            transformed = this.mSwitchTransformationMethod.getTransformation(text, this);
        } else {
            transformed = text;
        }
        return new StaticLayout(transformed, this.mTextPaint, transformed != null ? (int) Math.ceil((double) Layout.getDesiredWidth(transformed, this.mTextPaint)) : 0, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
    }

    private boolean hitThumb(float x, float y) {
        if (this.mThumbDrawable == null) {
            return false;
        }
        int thumbOffset = getThumbOffset();
        this.mThumbDrawable.getPadding(this.mTempRect);
        int thumbTop = this.mSwitchTop - this.mTouchSlop;
        int thumbLeft = (this.mSwitchLeft + thumbOffset) - this.mTouchSlop;
        int thumbRight = (((this.mThumbWidth + thumbLeft) + this.mTempRect.left) + this.mTempRect.right) + this.mTouchSlop;
        int thumbBottom = this.mSwitchBottom + this.mTouchSlop;
        if (x <= ((float) thumbLeft) || x >= ((float) thumbRight) || y <= ((float) thumbTop) || y >= ((float) thumbBottom)) {
            return false;
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        this.mVelocityTracker.addMovement(ev);
        float x;
        float y;
        switch (ev.getActionMasked()) {
            case FileUtils.ACTION_GET_FILE /*0*/:
                x = ev.getX();
                y = ev.getY();
                if (isEnabled() && hitThumb(x, y)) {
                    this.mTouchMode = TOUCH_MODE_DOWN;
                    this.mTouchX = x;
                    this.mTouchY = y;
                    break;
                }
            case TOUCH_MODE_DOWN /*1*/:
            case MONOSPACE /*3*/:
                if (this.mTouchMode != TOUCH_MODE_DRAGGING) {
                    this.mTouchMode = 0;
                    this.mVelocityTracker.clear();
                    break;
                }
                stopDrag(ev);
                super.onTouchEvent(ev);
                return true;
            case TOUCH_MODE_DRAGGING /*2*/:
                switch (this.mTouchMode) {
                    case FileUtils.ACTION_GET_FILE /*0*/:
                        break;
                    case TOUCH_MODE_DOWN /*1*/:
                        x = ev.getX();
                        y = ev.getY();
                        if (Math.abs(x - this.mTouchX) > ((float) this.mTouchSlop) || Math.abs(y - this.mTouchY) > ((float) this.mTouchSlop)) {
                            this.mTouchMode = TOUCH_MODE_DRAGGING;
                            getParent().requestDisallowInterceptTouchEvent(true);
                            this.mTouchX = x;
                            this.mTouchY = y;
                            return true;
                        }
                    case TOUCH_MODE_DRAGGING /*2*/:
                        float dPos;
                        x = ev.getX();
                        int thumbScrollRange = getThumbScrollRange();
                        float thumbScrollOffset = x - this.mTouchX;
                        if (thumbScrollRange != 0) {
                            dPos = thumbScrollOffset / ((float) thumbScrollRange);
                        } else {
                            dPos = thumbScrollOffset > 0.0f ? 1.0f : -1.0f;
                        }
                        if (ViewUtils.isLayoutRtl(this)) {
                            dPos = -dPos;
                        }
                        float newPos = constrain(this.mThumbPosition + dPos, 0.0f, 1.0f);
                        if (newPos != this.mThumbPosition) {
                            this.mTouchX = x;
                            setThumbPosition(newPos);
                        }
                        return true;
                    default:
                        break;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void cancelSuperTouch(MotionEvent ev) {
        MotionEvent cancel = MotionEvent.obtain(ev);
        cancel.setAction(MONOSPACE);
        super.onTouchEvent(cancel);
        cancel.recycle();
    }

    private void stopDrag(MotionEvent ev) {
        boolean commitChange;
        boolean newState;
        this.mTouchMode = 0;
        if (ev.getAction() == TOUCH_MODE_DOWN && isEnabled()) {
            commitChange = true;
        } else {
            commitChange = false;
        }
        boolean oldState = isChecked();
        if (commitChange) {
            this.mVelocityTracker.computeCurrentVelocity(PointerIconCompat.TYPE_DEFAULT);
            float xvel = this.mVelocityTracker.getXVelocity();
            newState = Math.abs(xvel) > ((float) this.mMinFlingVelocity) ? ViewUtils.isLayoutRtl(this) ? xvel < 0.0f : xvel > 0.0f : getTargetCheckedState();
        } else {
            newState = oldState;
        }
        if (newState != oldState) {
            playSoundEffect(0);
        }
        setChecked(newState);
        cancelSuperTouch(ev);
    }

    private void animateThumbToCheckedState(boolean newCheckedState) {
        float targetPosition = newCheckedState ? 1.0f : 0.0f;
        Property property = THUMB_POS;
        float[] fArr = new float[TOUCH_MODE_DOWN];
        fArr[0] = targetPosition;
        this.mPositionAnimator = ObjectAnimator.ofFloat(this, property, fArr);
        this.mPositionAnimator.setDuration(250);
        if (VERSION.SDK_INT >= 18) {
            this.mPositionAnimator.setAutoCancel(true);
        }
        this.mPositionAnimator.start();
    }

    private void cancelPositionAnimator() {
        if (this.mPositionAnimator != null) {
            this.mPositionAnimator.cancel();
        }
    }

    private boolean getTargetCheckedState() {
        return this.mThumbPosition > 0.5f;
    }

    void setThumbPosition(float position) {
        this.mThumbPosition = position;
        invalidate();
    }

    public void toggle() {
        setChecked(!isChecked());
    }

    public void setChecked(boolean checked) {
        super.setChecked(checked);
        checked = isChecked();
        if (getWindowToken() == null || !ViewCompat.isLaidOut(this)) {
            cancelPositionAnimator();
            setThumbPosition(checked ? 1.0f : 0.0f);
            return;
        }
        animateThumbToCheckedState(checked);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int switchLeft;
        int switchRight;
        int switchTop;
        int switchBottom;
        super.onLayout(changed, left, top, right, bottom);
        int opticalInsetLeft = 0;
        int opticalInsetRight = 0;
        if (this.mThumbDrawable != null) {
            Rect trackPadding = this.mTempRect;
            if (this.mTrackDrawable != null) {
                this.mTrackDrawable.getPadding(trackPadding);
            } else {
                trackPadding.setEmpty();
            }
            Rect insets = DrawableUtils.getOpticalBounds(this.mThumbDrawable);
            opticalInsetLeft = Math.max(0, insets.left - trackPadding.left);
            opticalInsetRight = Math.max(0, insets.right - trackPadding.right);
        }
        if (ViewUtils.isLayoutRtl(this)) {
            switchLeft = getPaddingLeft() + opticalInsetLeft;
            switchRight = ((this.mSwitchWidth + switchLeft) - opticalInsetLeft) - opticalInsetRight;
        } else {
            switchRight = (getWidth() - getPaddingRight()) - opticalInsetRight;
            switchLeft = ((switchRight - this.mSwitchWidth) + opticalInsetLeft) + opticalInsetRight;
        }
        switch (getGravity() & C0173R.styleable.AppCompatTheme_spinnerStyle) {
            case TransportMediator.FLAG_KEY_MEDIA_PAUSE /*16*/:
                switchTop = (((getPaddingTop() + getHeight()) - getPaddingBottom()) / TOUCH_MODE_DRAGGING) - (this.mSwitchHeight / TOUCH_MODE_DRAGGING);
                switchBottom = switchTop + this.mSwitchHeight;
                break;
            case C0173R.styleable.AppCompatTheme_panelMenuListWidth /*80*/:
                switchBottom = getHeight() - getPaddingBottom();
                switchTop = switchBottom - this.mSwitchHeight;
                break;
            default:
                switchTop = getPaddingTop();
                switchBottom = switchTop + this.mSwitchHeight;
                break;
        }
        this.mSwitchLeft = switchLeft;
        this.mSwitchTop = switchTop;
        this.mSwitchBottom = switchBottom;
        this.mSwitchRight = switchRight;
    }

    public void draw(Canvas c) {
        Rect thumbInsets;
        Rect padding = this.mTempRect;
        int switchLeft = this.mSwitchLeft;
        int switchTop = this.mSwitchTop;
        int switchRight = this.mSwitchRight;
        int switchBottom = this.mSwitchBottom;
        int thumbInitialLeft = switchLeft + getThumbOffset();
        if (this.mThumbDrawable != null) {
            thumbInsets = DrawableUtils.getOpticalBounds(this.mThumbDrawable);
        } else {
            thumbInsets = DrawableUtils.INSETS_NONE;
        }
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.getPadding(padding);
            thumbInitialLeft += padding.left;
            int trackLeft = switchLeft;
            int trackTop = switchTop;
            int trackRight = switchRight;
            int trackBottom = switchBottom;
            if (thumbInsets != null) {
                int i = thumbInsets.left;
                int i2 = padding.left;
                if (i > r0) {
                    trackLeft += thumbInsets.left - padding.left;
                }
                i = thumbInsets.top;
                i2 = padding.top;
                if (i > r0) {
                    trackTop += thumbInsets.top - padding.top;
                }
                i = thumbInsets.right;
                i2 = padding.right;
                if (i > r0) {
                    trackRight -= thumbInsets.right - padding.right;
                }
                i = thumbInsets.bottom;
                i2 = padding.bottom;
                if (i > r0) {
                    trackBottom -= thumbInsets.bottom - padding.bottom;
                }
            }
            this.mTrackDrawable.setBounds(trackLeft, trackTop, trackRight, trackBottom);
        }
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.getPadding(padding);
            int thumbLeft = thumbInitialLeft - padding.left;
            int thumbRight = (this.mThumbWidth + thumbInitialLeft) + padding.right;
            this.mThumbDrawable.setBounds(thumbLeft, switchTop, thumbRight, switchBottom);
            Drawable background = getBackground();
            if (background != null) {
                DrawableCompat.setHotspotBounds(background, thumbLeft, switchTop, thumbRight, switchBottom);
            }
        }
        super.draw(c);
    }

    protected void onDraw(Canvas canvas) {
        int saveCount;
        Layout switchText;
        super.onDraw(canvas);
        Rect padding = this.mTempRect;
        Drawable trackDrawable = this.mTrackDrawable;
        if (trackDrawable != null) {
            trackDrawable.getPadding(padding);
        } else {
            padding.setEmpty();
        }
        int switchTop = this.mSwitchTop;
        int switchBottom = this.mSwitchBottom;
        int switchInnerTop = switchTop + padding.top;
        int switchInnerBottom = switchBottom - padding.bottom;
        Drawable thumbDrawable = this.mThumbDrawable;
        if (trackDrawable != null) {
            if (!this.mSplitTrack || thumbDrawable == null) {
                trackDrawable.draw(canvas);
            } else {
                Rect insets = DrawableUtils.getOpticalBounds(thumbDrawable);
                thumbDrawable.copyBounds(padding);
                padding.left += insets.left;
                padding.right -= insets.right;
                saveCount = canvas.save();
                canvas.clipRect(padding, Op.DIFFERENCE);
                trackDrawable.draw(canvas);
                canvas.restoreToCount(saveCount);
            }
        }
        saveCount = canvas.save();
        if (thumbDrawable != null) {
            thumbDrawable.draw(canvas);
        }
        if (getTargetCheckedState()) {
            switchText = this.mOnLayout;
        } else {
            switchText = this.mOffLayout;
        }
        if (switchText != null) {
            int cX;
            int[] drawableState = getDrawableState();
            if (this.mTextColors != null) {
                this.mTextPaint.setColor(this.mTextColors.getColorForState(drawableState, 0));
            }
            this.mTextPaint.drawableState = drawableState;
            if (thumbDrawable != null) {
                Rect bounds = thumbDrawable.getBounds();
                cX = bounds.left + bounds.right;
            } else {
                cX = getWidth();
            }
            canvas.translate((float) ((cX / TOUCH_MODE_DRAGGING) - (switchText.getWidth() / TOUCH_MODE_DRAGGING)), (float) (((switchInnerTop + switchInnerBottom) / TOUCH_MODE_DRAGGING) - (switchText.getHeight() / TOUCH_MODE_DRAGGING)));
            switchText.draw(canvas);
        }
        canvas.restoreToCount(saveCount);
    }

    public int getCompoundPaddingLeft() {
        if (!ViewUtils.isLayoutRtl(this)) {
            return super.getCompoundPaddingLeft();
        }
        int padding = super.getCompoundPaddingLeft() + this.mSwitchWidth;
        if (TextUtils.isEmpty(getText())) {
            return padding;
        }
        return padding + this.mSwitchPadding;
    }

    public int getCompoundPaddingRight() {
        if (ViewUtils.isLayoutRtl(this)) {
            return super.getCompoundPaddingRight();
        }
        int padding = super.getCompoundPaddingRight() + this.mSwitchWidth;
        if (TextUtils.isEmpty(getText())) {
            return padding;
        }
        return padding + this.mSwitchPadding;
    }

    private int getThumbOffset() {
        float thumbPosition;
        if (ViewUtils.isLayoutRtl(this)) {
            thumbPosition = 1.0f - this.mThumbPosition;
        } else {
            thumbPosition = this.mThumbPosition;
        }
        return (int) ((((float) getThumbScrollRange()) * thumbPosition) + 0.5f);
    }

    private int getThumbScrollRange() {
        if (this.mTrackDrawable == null) {
            return 0;
        }
        Rect insets;
        Rect padding = this.mTempRect;
        this.mTrackDrawable.getPadding(padding);
        if (this.mThumbDrawable != null) {
            insets = DrawableUtils.getOpticalBounds(this.mThumbDrawable);
        } else {
            insets = DrawableUtils.INSETS_NONE;
        }
        return ((((this.mSwitchWidth - this.mThumbWidth) - padding.left) - padding.right) - insets.left) - insets.right;
    }

    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + TOUCH_MODE_DOWN);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int[] state = getDrawableState();
        boolean changed = false;
        Drawable thumbDrawable = this.mThumbDrawable;
        if (thumbDrawable != null && thumbDrawable.isStateful()) {
            changed = false | thumbDrawable.setState(state);
        }
        Drawable trackDrawable = this.mTrackDrawable;
        if (trackDrawable != null && trackDrawable.isStateful()) {
            changed |= trackDrawable.setState(state);
        }
        if (changed) {
            invalidate();
        }
    }

    public void drawableHotspotChanged(float x, float y) {
        if (VERSION.SDK_INT >= 21) {
            super.drawableHotspotChanged(x, y);
        }
        if (this.mThumbDrawable != null) {
            DrawableCompat.setHotspot(this.mThumbDrawable, x, y);
        }
        if (this.mTrackDrawable != null) {
            DrawableCompat.setHotspot(this.mTrackDrawable, x, y);
        }
    }

    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == this.mThumbDrawable || who == this.mTrackDrawable;
    }

    public void jumpDrawablesToCurrentState() {
        if (VERSION.SDK_INT >= 14) {
            super.jumpDrawablesToCurrentState();
            if (this.mThumbDrawable != null) {
                this.mThumbDrawable.jumpToCurrentState();
            }
            if (this.mTrackDrawable != null) {
                this.mTrackDrawable.jumpToCurrentState();
            }
            if (this.mPositionAnimator != null && this.mPositionAnimator.isStarted()) {
                this.mPositionAnimator.end();
                this.mPositionAnimator = null;
            }
        }
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(ACCESSIBILITY_EVENT_CLASS_NAME);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        if (VERSION.SDK_INT >= 14) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setClassName(ACCESSIBILITY_EVENT_CLASS_NAME);
            CharSequence switchText = isChecked() ? this.mTextOn : this.mTextOff;
            if (!TextUtils.isEmpty(switchText)) {
                CharSequence oldText = info.getText();
                if (TextUtils.isEmpty(oldText)) {
                    info.setText(switchText);
                    return;
                }
                StringBuilder newText = new StringBuilder();
                newText.append(oldText).append(' ').append(switchText);
                info.setText(newText);
            }
        }
    }

    private static float constrain(float amount, float low, float high) {
        if (amount < low) {
            return low;
        }
        return amount > high ? high : amount;
    }
}
