package hp.redreader.com.di.module

import com.jess.arms.base.BaseApplication
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.utils.ArmsUtils

import dagger.Module
import dagger.Provides

import hp.redreader.com.mvp.contract.MainContract
import hp.redreader.com.mvp.model.MainModel


@Module
//构建MainModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class MainModule(private val view: MainContract.View) {
    @ActivityScope
    @Provides
    fun provideMainView(): MainContract.View {
        return this.view
    }

    @ActivityScope
    @Provides
    fun provideMainModel(model: MainModel): MainContract.Model {
        return model
    }
}
