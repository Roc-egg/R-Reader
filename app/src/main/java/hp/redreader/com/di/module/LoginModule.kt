package hp.redreader.com.di.module

import com.jess.arms.di.scope.ActivityScope

import dagger.Module
import dagger.Provides

import hp.redreader.com.mvp.contract.LoginContract
import hp.redreader.com.mvp.model.LoginModel


@Module
//构建LoginModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class LoginModule(private val view: LoginContract.View) {
    @ActivityScope
    @Provides
    fun provideLoginView(): LoginContract.View {
        return this.view
    }

    @ActivityScope
    @Provides
    fun provideLoginModel(model: LoginModel): LoginContract.Model {
        return model
    }
}
