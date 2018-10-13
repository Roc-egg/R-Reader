package hp.redreader.com.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import hp.redreader.com.di.module.SplashModule

import com.jess.arms.di.scope.ActivityScope
import hp.redreader.com.mvp.ui.activity.SplashActivity

@ActivityScope
@Component(modules = arrayOf(SplashModule::class), dependencies = arrayOf(AppComponent::class))
interface SplashComponent {
    fun inject(activity: SplashActivity)
}
