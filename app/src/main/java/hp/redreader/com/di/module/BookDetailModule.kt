package hp.redreader.com.di.module

import com.jess.arms.di.scope.ActivityScope

import dagger.Module
import dagger.Provides

import hp.redreader.com.mvp.contract.BookDetailContract
import hp.redreader.com.mvp.model.BookDetailModel


@Module
//构建BookDetailModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class BookDetailModule(private val view: BookDetailContract.View) {
    @ActivityScope
    @Provides
    fun provideBookDetailView(): BookDetailContract.View {
        return this.view
    }

    @ActivityScope
    @Provides
    fun provideBookDetailModel(model: BookDetailModel): BookDetailContract.Model {
        return model
    }
}
