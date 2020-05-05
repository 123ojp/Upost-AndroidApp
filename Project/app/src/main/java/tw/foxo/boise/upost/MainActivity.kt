package tw.foxo.boise.upost

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.nav_header_main.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import tw.foxo.boise.upost.databaseHandler.SettingDbHelper
import tw.foxo.boise.upost.eventBus.EVENT_ACCOUNTRELAOD
import tw.foxo.boise.upost.eventBus.EVENT_BOARDCKICK
import tw.foxo.boise.upost.eventBus.EventMessage
import tw.foxo.boise.upost.networkHandler.*
import tw.foxo.boise.upost.networkjsonObj.BoardObj
import tw.foxo.boise.upost.networkjsonObj.PostFgObj
import tw.foxo.boise.upost.networkjsonObj.postView.ISPOST
import tw.foxo.boise.upost.ui.create.CreateCommitView
import tw.foxo.boise.upost.ui.create.CreatePostView
import tw.foxo.boise.upost.ui.home.*
import tw.foxo.boise.upost.ui.post.PostVIew
import tw.foxo.boise.upost.ui.register.RegisterActivity
import java.io.IOException
import java.security.AccessController.getContext

class MainActivity : AppCompatActivity() , HomeBoardListFgFragment.OnListFragmentInteractionListener, HomeViewPostHotFragment.OnListFragmentInteractionListener {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.create_post)
        fab.setOnClickListener {
            val intent = Intent(this, CreatePostView::class.java)
            intent.putExtra(ISPOST,false)
            startActivityForResult(intent,12)
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        //eventBus
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        } else {
            Toast.makeText(this, "請勿重複註冊事件", Toast.LENGTH_SHORT).show()
        }

    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun event(eventBusMsg: EventMessage) {
        //Toast.makeText(activity,eventBusMsg.eventName,Toast.LENGTH_SHORT).show()
        if (eventBusMsg.eventName== EVENT_ACCOUNTRELAOD){
            freshAccountStatus()

            var fragmentManager = supportFragmentManager;
            fragmentManager.popBackStack()


        }
    }

    override fun onStart() {
        super.onStart()
        freshAccountStatus()
        viewPager.setCurrentItem(1)


    }
    fun freshAccountStatus(){
        val dbHelper =  SettingDbHelper(this)
        if (dbHelper.isToken()!!){
            var mHandler =  Handler(Looper.getMainLooper());
            var base_req = getRequests(this).getUserInfoBase()
            val okHttpClient = OkHttpClient()
            okHttpClient.newCall(base_req).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    mHandler.post {
                        Toast.makeText(this@MainActivity, NETWORKERROR, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val getHand = GetHandler(response)
                    val retj = getHand.jsonRes
                    if (retj != null) {
                        mHandler.post {
                            nav_userEmail.text = retj.getString(EMAIL)
                            nav_userName.text = retj.getString(USERNAME)
                            nav_userSchool.text = retj.getString(B_SCHOOL)
                            if (retj.getString(SEX) == BOY) {
                                nav_userIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor(BOY_ICON))
                            } else if (retj.getString(SEX) == GIRL){
                                nav_userIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor(GIRL_ICON))

                            }

                        }
                    }
                }

            })
        }
    }
    //eventBus
    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        } else {
            //Toast.makeText(this, "請勿重複註冊事件", Toast.LENGTH_SHORT).show()
        }
    }
    //eventBus end
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    override fun onListFragmentInteraction(item: BoardObj?) {
        if (item != null) {
            if (item.isClickAble){
                viewPager.setCurrentItem(1)
                val event = EventMessage(EVENT_BOARDCKICK,item.board_id)
                EventBus.getDefault().postSticky(event)
            }
        }
    }


    override fun onListFragmentInteraction(item: PostFgObj?) {
        if (item != null) {
            if (item.isClickAble){
                val intent = Intent(this, PostVIew::class.java)
                intent.putExtra(POSTID,item.post_id.toString())
                startActivityForResult(intent,12)
            }
        }
    }


}
