package android.support.v4.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.annotation.RequiresApi;
import android.view.View;

@RequiresApi(14)
class ViewPropertyAnimatorCompatICS {

    /* renamed from: android.support.v4.view.ViewPropertyAnimatorCompatICS.1 */
    static class C00961 extends AnimatorListenerAdapter {
        final /* synthetic */ ViewPropertyAnimatorListener val$listener;
        final /* synthetic */ View val$view;

        C00961(ViewPropertyAnimatorListener viewPropertyAnimatorListener, View view) {
            this.val$listener = viewPropertyAnimatorListener;
            this.val$view = view;
        }

        public void onAnimationCancel(Animator animation) {
            this.val$listener.onAnimationCancel(this.val$view);
        }

        public void onAnimationEnd(Animator animation) {
            this.val$listener.onAnimationEnd(this.val$view);
        }

        public void onAnimationStart(Animator animation) {
            this.val$listener.onAnimationStart(this.val$view);
        }
    }

    ViewPropertyAnimatorCompatICS() {
    }

    public static void setListener(View view, ViewPropertyAnimatorListener listener) {
        if (listener != null) {
            view.animate().setListener(new C00961(listener, view));
        } else {
            view.animate().setListener(null);
        }
    }
}
