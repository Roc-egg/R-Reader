package hp.redreader.com.mvp.ui.widegt;

import android.animation.Animator;
import android.transition.ChangeBounds;
import android.transition.TransitionValues;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

/**
 * 类名：    RedReader
 * 类描述：
 * 创建人：  hp
 * 创建时间：2018/9/23/023 21:49
 * 修改人：  hp
 * 修改时间：2018/9/23/023 21:49
 * 修改备注：
 */
public class CustomChangeBounds extends ChangeBounds {

    @Override
    public Animator createAnimator(final ViewGroup sceneRoot,
                                   TransitionValues startValues,
                                   final TransitionValues endValues) {

        Animator changeBounds = super.createAnimator(sceneRoot, startValues, endValues);
        if (startValues == null || endValues == null || changeBounds == null) {
            return null;
        }

//        if (endValues.view instanceof ViewGroup) {
//            ViewGroup vg = (ViewGroup) endValues.view;
//            float offset = vg.getHeight() / 3;
//            for (int i = 0; i < vg.getChildCount(); i++) {
//                View v = vg.getChildAt(i);
//                v.setTranslationY(offset);
//                v.setAlpha(0f);
//                v.animate()
//                        .alpha(1f)
//                        .translationY(0f)
//                        .setDuration(150)
//                        .setStartDelay(150)
//                        .setInterpolator(AnimationUtils.loadInterpolator(vg.getContext(),
//                                android.R.interpolator.fast_out_slow_in));
//                offset *= 1.8f;
//            }
//        }

        changeBounds.setDuration(500);
        changeBounds.setInterpolator(AnimationUtils.loadInterpolator(sceneRoot.getContext(),
                android.R.interpolator.fast_out_slow_in));
        return changeBounds;
    }
}
