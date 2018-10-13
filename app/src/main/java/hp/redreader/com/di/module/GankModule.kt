package hp.redreader.com.di.module

import com.jess.arms.di.scope.FragmentScope

import dagger.Module
import dagger.Provides

import hp.redreader.com.mvp.contract.GankContract
import hp.redreader.com.mvp.model.GankModel


@Module
//构建GankModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class GankModule(private val view: GankContract.View) {
    @FragmentScope
    @Provides
    fun provideGankView(): GankContract.View {
        return this.view
    }

    @FragmentScope
    @Provides
    fun provideGankModel(model: GankModel): GankContract.Model {
        return model
    }
}
