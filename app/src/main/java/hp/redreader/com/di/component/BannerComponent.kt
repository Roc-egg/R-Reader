package hp.redreader.com.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import hp.redreader.com.di.module.BannerModule

import com.jess.arms.di.scope.FragmentScope
import hp.redreader.com.mvp.ui.activity.wan.ArticleListActivity
import hp.redreader.com.mvp.ui.fragment.wanChild.BannerFragment

@FragmentScope
@Component(modules = arrayOf(BannerModule::class), dependencies = arrayOf(AppComponent::class))
interface BannerComponent {
    fun inject(fragment: BannerFragment)
    fun inject(activity: ArticleListActivity)
}
