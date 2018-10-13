package hp.redreader.com.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import hp.redreader.com.di.module.BookDetailModule

import com.jess.arms.di.scope.ActivityScope
import hp.redreader.com.mvp.ui.activity.wan.BookDetailActivity

@ActivityScope
@Component(modules = arrayOf(BookDetailModule::class), dependencies = arrayOf(AppComponent::class))
interface BookDetailComponent {
    fun inject(activity: BookDetailActivity)
}
