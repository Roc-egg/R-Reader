package hp.redreader.com.di.module

import com.jess.arms.di.scope.ActivityScope

import dagger.Module
import dagger.Provides

import hp.redreader.com.mvp.contract.SplashContract
import hp.redreader.com.mvp.model.SplashModel


@Module
//构建SplashModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class SplashModule(private val view: SplashContract.View) {
    @ActivityScope
    @Provides
    fun provideSplashView(): SplashContract.View {
        return this.view
    }

    @ActivityScope
    @Provides
    fun provideSplashModel(model: SplashModel): SplashContract.Model {
        return model
    }
}
