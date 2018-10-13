package hp.redreader.com.di.module

import com.jess.arms.di.scope.FragmentScope

import dagger.Module
import dagger.Provides

import hp.redreader.com.mvp.contract.WanContract
import hp.redreader.com.mvp.model.WanModel


@Module
//构建WanModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class WanModule(private val view: WanContract.View) {
    @FragmentScope
    @Provides
    fun provideWanView(): WanContract.View {
        return this.view
    }

    @FragmentScope
    @Provides
    fun provideWanModel(model: WanModel): WanContract.Model {
        return model
    }
}
