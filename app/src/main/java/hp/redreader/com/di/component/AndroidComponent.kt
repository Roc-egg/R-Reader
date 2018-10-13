package hp.redreader.com.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import hp.redreader.com.di.module.AndroidModule

import com.jess.arms.di.scope.FragmentScope
import hp.redreader.com.mvp.ui.fragment.gankChild.AndroidFragment

@FragmentScope
@Component(modules = arrayOf(AndroidModule::class), dependencies = arrayOf(AppComponent::class))
interface AndroidComponent {
    fun inject(fragment: AndroidFragment)
}
