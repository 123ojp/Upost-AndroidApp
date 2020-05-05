package tw.foxo.boise.upost.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_home.*
import tw.foxo.boise.upost.R

class HomeFragment : Fragment() {
//https://www.youtube.com/watch?v=SHFM3Qw43LE
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        //val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(this, Observer {
        //    textView.text = it
        })


        val adapter = getFragmentManager()?.let { MyViewPagerAdapter(it) }
        if (adapter != null) {
            adapter.addFragment(HomeBoardListFgFragment() , "Board")
            adapter.addFragment(HomeViewPostHotFragment() , "Hot Post")
            adapter.addFragment(HomeViewPostNewFragment() , "New Post")
            root.findViewById<ViewPager>(R.id.viewPager).adapter = adapter

            root.findViewById<TabLayout>(R.id.tabs).setupWithViewPager(root.findViewById<ViewPager>(R.id.viewPager))
        }

        return root
    }
    class MyViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager){

        private val fragmentList : MutableList<Fragment> = ArrayList()
        private val titleList : MutableList<String> = ArrayList()

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        fun addFragment(fragment: Fragment,title:String){
            fragmentList.add(fragment)
            titleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titleList[position]
        }

    }
}