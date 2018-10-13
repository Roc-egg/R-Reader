package hp.redreader.com.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import hp.redreader.com.di.module.WelfareModule

import com.jess.arms.di.scope.FragmentScope
import hp.redreader.com.mvp.ui.fragment.gankChild.WelfareFragment

@FragmentScope
@Component(modules = arrayOf(WelfareModule::class), dependencies = arrayOf(AppComponent::class))
interface WelfareComponent {
    fun inject(fragment: WelfareFragment)
}
