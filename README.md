# float-layout
# Tạo một modue cho app
```Java  SheetLayout.java ```
```Java 
public class SheetLayout extends FrameLayout {

    private static final int DEFAULT_ANIMATION_DURATION = 350;
    private static final int DEFAULT_FAB_SIZE = 56;

    private static final int FAB_CIRCLE = 0;
    private static final int FAB_EXPAND = 1;

    @IntDef({FAB_CIRCLE, FAB_EXPAND})
    private @interface Fab {
    }

    private LinearLayout mFabExpandLayout;
    private ImageView mFab;

    int mFabType = FAB_CIRCLE;
    boolean mAnimatingFab = false;
    private int animationDuration;
    private int mFabSize;

    OnFabAnimationEndListener mListener;

    public SheetLayout(Context context) {
        super(context);
        init();
    }

    public SheetLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        loadAttributes(context, attrs);
    }

    public SheetLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        loadAttributes(context, attrs);
    }

    private void init() {
        inflate(getContext(), R.layout.bottom_sheet_layout, this);
        mFabExpandLayout = ((LinearLayout) findViewById(R.id.container));
    }

    private void loadAttributes(Context context, AttributeSet attrs) {
        TypedValue outValue = new TypedValue();
        Resources.Theme theme = context.getTheme();

        // use ?attr/colorPrimary as background color
        theme.resolveAttribute(R.attr.colorPrimary, outValue, true);

        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.FooterLayout,
                0, 0);

        int containerGravity;
        try {
            setColor(a.getColor(R.styleable.FooterLayout_ft_color,
                    outValue.data));
            animationDuration = a.getInteger(R.styleable.FooterLayout_ft_anim_duration,
                    DEFAULT_ANIMATION_DURATION);
            containerGravity = a.getInteger(R.styleable.FooterLayout_ft_container_gravity, 1);
            mFabSize = a.getInteger(R.styleable.FooterLayout_ft_fab_type, DEFAULT_FAB_SIZE);
        } finally {
            a.recycle();
        }

        mFabExpandLayout.setGravity(getGravity(containerGravity));
    }

    public void setFab(ImageView imageView) {
        mFab = imageView;
    }

    private int getGravity(int gravityEnum) {
        return (gravityEnum == 0 ? Gravity.START
                : gravityEnum == 1 ? Gravity.CENTER_HORIZONTAL : Gravity.END)
                | Gravity.CENTER_VERTICAL;
    }

    public void setColor(int color) {
        mFabExpandLayout.setBackgroundColor(color);
    }

    @Override
    public void addView(@NonNull View child) {
        if (canAddViewToContainer()) {
            mFabExpandLayout.addView(child);
        } else {
            super.addView(child);
        }
    }

    @Override
    public void addView(@NonNull View child, int width, int height) {
        if (canAddViewToContainer()) {
            mFabExpandLayout.addView(child, width, height);
        } else {
            super.addView(child, width, height);
        }
    }

    @Override
    public void addView(@NonNull View child, ViewGroup.LayoutParams params) {
        if (canAddViewToContainer()) {
            mFabExpandLayout.addView(child, params);
        } else {
            super.addView(child, params);
        }
    }

    @Override
    public void addView(@NonNull View child, int index, ViewGroup.LayoutParams params) {
        if (canAddViewToContainer()) {
            mFabExpandLayout.addView(child, index, params);
        } else {
            super.addView(child, index, params);
        }
    }

    /**
     * hide() and show() methods are useful for remembering the toolbar state on screen rotation.
     */
    public void hide() {
        mFabExpandLayout.setVisibility(View.INVISIBLE);
        mFabType = FAB_CIRCLE;
    }

    public void show() {
        mFabExpandLayout.setVisibility(View.VISIBLE);
        mFabType = FAB_EXPAND;
    }

    private boolean canAddViewToContainer() {
        return mFabExpandLayout != null;
    }

    public void expandFab() {
        mFabType = FAB_EXPAND;
        mAnimatingFab = true;

        // Center point on the screen of the FAB.
        int x = (int) (centerX(mFab));
        int y = (int) (centerY(mFab));

        // Start and end radius of the sheet expand animation.
        float startRadius = getFabSizePx() / 2;
        float endRadius = calculateStartRadius(x, y);

        mFabExpandLayout.setAlpha(0f);
        mFabExpandLayout.setVisibility(View.VISIBLE);

        mFab.setVisibility(View.INVISIBLE);
        mFab.setTranslationX(0f);
        mFab.setTranslationY(0f);

        mFab.setAlpha(1f);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            expandPreLollipop(x, y, startRadius, endRadius);
        } else {
            expandLollipop(x, y, startRadius, endRadius);
        }
    }

    public void contractFab() {
        if (!isFabExpanded()) {
            return;
        }

        mFabType = FAB_CIRCLE;
        mAnimatingFab = true;

        mFab.setAlpha(0f);
        mFab.setVisibility(View.VISIBLE);

        // Center point on the screen of the FAB.
        int x = (int) (centerX(mFab));
        int y = (int) (centerY(mFab));

        // Start and end radius of the toolbar contract animation.
        float endRadius = getFabSizePx() / 2;
        float startRadius = calculateStartRadius(x, y);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            contractPreLollipop(x, y, startRadius, endRadius);
        } else {
            contractLollipop(x, y, startRadius, endRadius);
        }
    }

    public boolean isFabExpanded() {
        return mFabType == FAB_EXPAND;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void expandLollipop(int x, int y, float startRadius, float endRadius) {

        Animator toolbarExpandAnim = ViewAnimationUtils.createCircularReveal(
                mFabExpandLayout, x, y, startRadius, endRadius);
        toolbarExpandAnim.setDuration(animationDuration);
        toolbarExpandAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mFabExpandLayout.setAlpha(1f);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                expandAnimationEnd();

            }
        });

        toolbarExpandAnim.start();
    }

    private void expandPreLollipop(int x, int y, float startRadius, float endRadius) {

        SupportAnimator toolbarExpandAnim = io.codetail.animation.ViewAnimationUtils
                .createCircularReveal(
                        mFabExpandLayout, x, y, startRadius, endRadius);
        toolbarExpandAnim.setDuration(animationDuration);
        toolbarExpandAnim.addListener(new SupportAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart() {
                mFabExpandLayout.setAlpha(1f);
            }

            @Override
            public void onAnimationEnd() {
                //mFab.setAlpha(1f);
                expandAnimationEnd();

            }

            @Override
            public void onAnimationCancel() {

            }

            @Override
            public void onAnimationRepeat() {

            }
        });

        toolbarExpandAnim.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void contractLollipop(int x, int y, float startRadius, float endRadius) {

        Animator toolbarContractAnim = ViewAnimationUtils.createCircularReveal(
                mFabExpandLayout, x, y, startRadius, endRadius);
        toolbarContractAnim.setDuration(animationDuration);

        toolbarContractAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                contractAnimationEnd();
            }
        });

        toolbarContractAnim.start();
    }

    private void contractPreLollipop(int x, int y, float startRadius, float endRadius) {

        final SupportAnimator toolbarContractAnim = io.codetail.animation.ViewAnimationUtils
                .createCircularReveal(mFabExpandLayout, x, y, startRadius, endRadius);
        toolbarContractAnim.setDuration(animationDuration);

        toolbarContractAnim.addListener(new SupportAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
                contractAnimationEnd();
            }

            @Override
            public void onAnimationCancel() {

            }

            @Override
            public void onAnimationRepeat() {

            }
        });

        toolbarContractAnim.start();
    }

    private void expandAnimationEnd() {
        mAnimatingFab = false;
        if (mListener != null)
            mListener.onFabAnimationEnd();
    }

    private void contractAnimationEnd() {
        mFab.setAlpha(1f);
        mFabExpandLayout.setAlpha(0f);

        mAnimatingFab = false;
        mFabExpandLayout.setVisibility(View.INVISIBLE);
        mFabExpandLayout.setAlpha(1f);
    }

    private float calculateStartRadius(int x, int y) {
        return (float) Math.hypot(
                Math.max(x, mFabExpandLayout.getWidth() - x),
                Math.max(y, mFabExpandLayout.getHeight() - y));
    }

    private int getFabSizePx() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(mFabSize * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private float centerX(View view) {
        return ViewCompat.getX(view) + view.getWidth() / 2f;
    }

    private float centerY(View view) {
        return ViewCompat.getY(view) + view.getHeight() / 2f;
    }

    public interface OnFabAnimationEndListener {
        void onFabAnimationEnd();
    }

    public void setFabAnimationEndListener(OnFabAnimationEndListener eventListener) {
        mListener = eventListener;
    }
}
```
## bottom_sheet_layout.xml
```XML 
<?xml version="1.0" encoding="utf-8"?>
<io.codetail.widget.RevealLinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

        <LinearLayout
            android:id="@+id/container"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:elevation="@dimen/elevation"
            android:gravity="center"
            android:orientation="horizontal"
            android:visibility="invisible"/>

</io.codetail.widget.RevealLinearLayout>
```
# trong anim
# activity_close_translate_to_bottom.xml
```XML 
<?xml version="1.0" encoding="utf-8"?>

<set xmlns:android="http://schemas.android.com/apk/res/android">

    <translate android:fromYDelta="0%"
        android:toYDelta="100%"
        android:duration="200"
        android:interpolator="@android:anim/decelerate_interpolator" />

</set>

```
##activity_no_animation.xml

```XML 
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android"
    android:fillAfter="true" >

    <translate android:fromYDelta="0%"
        android:toYDelta="0%"
        android:duration="200"
        android:interpolator="@android:anim/decelerate_interpolator" />

</set>


```
##activity_open_translate_from_bottom.xml


```XML
<?xml version="1.0" encoding="utf-8"?>

<set xmlns:android="http://schemas.android.com/apk/res/android">

    <translate android:fromYDelta="100%"
        android:toYDelta="0%"
        android:duration="150"
        android:interpolator="@android:anim/decelerate_interpolator" />

</set>
```
#value
##attrs.xml
```XmL 
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <declare-styleable name="FooterLayout">
        <attr name="ft_color" format="color"/>
        <attr name="ft_anim_duration" format="integer"/>
        <attr name="ft_container_gravity" format="enum">
            <enum name="start" value="0"/>
            <enum name="center" value="1"/>
            <enum name="end" value="2"/>
        </attr>
        <attr name="ft_fab_type" format="enum">
            <enum name="mini" value="40"/>
            <enum name="normal" value="56"/>
        </attr>
    </declare-styleable>
</resources>
```
##dimen.xml
```XML
 <!-- Default screen margins, per the Android Design guidelines. -->
    <dimen name="activity_horizontal_margin">16dp</dimen>
    <dimen name="activity_vertical_margin">16dp</dimen>
    <dimen name="elevation">4dp</dimen>
```
```XML

    <!-- Base application theme. -->
    <style name="AppTheme" parent="Theme.AppCompat.Light.NoActionBar">
        <!-- Customize your theme here. -->
        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>
    </style>

    <style name="ToolBarStyle" parent="">
        <item name="popupTheme">@style/ThemeOverlay.AppCompat.Light</item>
        <item name="android:theme">@style/ThemeOverlay.AppCompat.Dark</item>
    </style>

    <style name="ToolbarTitle" parent="@style/TextAppearance.Widget.AppCompat.Toolbar.Title">
        <item name="android:textSize">18sp</item>
    </style>
</resources>
```
##compile
```xml 

repositories {
    maven {
        url "https://jitpack.io"
    }
}
  compile('com.github.ozodrukh:CircularReveal:1.1.1@aar') {
        transitive = true;
    }
    compile 'com.jakewharton:butterknife:5.1.1'
    compile 'com.android.support:design:23.1.1'
```
###----------------------------------------------------------------------------------------------------------
