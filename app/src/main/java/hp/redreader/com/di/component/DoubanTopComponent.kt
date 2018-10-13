package hp.redreader.com.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import hp.redreader.com.di.module.DoubanTopModule

import com.jess.arms.di.scope.ActivityScope
import hp.redreader.com.mvp.ui.activity.movie.DoubanTopActivity

@ActivityScope
@Component(modules = arrayOf(DoubanTopModule::class), dependencies = arrayOf(AppComponent::class))
interface DoubanTopComponent {
    fun inject(activity: DoubanTopActivity)
}
