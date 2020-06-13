package com.prerevise.grabvid

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.prerevise.grabvid.fragments.download
import com.prerevise.grabvid.statussaver.StatusSaverMainFragment
import com.prerevise.grabvid.utils.IOUtils
import com.prerevise.grabvid.utils.iUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    val REQUEST_PERMISSION_CODE = 1001
    val REQUEST_PERMISSION = android.Manifest.permission.WRITE_EXTERNAL_STORAGE

    var viewPager: ViewPager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!isNeedGrantPermission()){
            setLayout()
        }
    }

    fun setLayout(){
        bottom_menu.setOnItemSelectedListener { id ->
            when (id){
                R.id.home -> viewpager.currentItem = 0
                R.id.statussaver -> viewpager.currentItem = 1
                R.id.downloads -> viewpager.currentItem = 2
                R.id.profile -> viewpager.currentItem = 3
            }
        }
        viewpager.setOnTouchListener({v, event -> true })
        setupViewPager(viewpager)
    }

    private fun setupViewPager(viewPager: ViewPager){
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(download(),"Home")
        adapter.addFragment(StatusSaverMainFragment(),"Status")
        viewPager.adapter = adapter

    }

    internal inner class ViewPagerAdapter(manager: FragmentManager):
    FragmentPagerAdapter(manager){
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String){
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

    private fun isNeedGrantPermission(): Boolean {
        try {
            if (IOUtils.hasMarsallow()){
                if (ContextCompat.checkSelfPermission(this,REQUEST_PERMISSION) != PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, REQUEST_PERMISSION)){
                        val msg = String.format(getString(R.string.format_request_permission), getString(R.string.app_name))
                        val localBuilder = AlertDialog.Builder(this@MainActivity)
                        localBuilder.setTitle(getString(R.string.permission_title))
                        localBuilder.setMessage(msg).setNeutralButton(getString(R.string.grant_option)){
                            paramAnonymousDialogInterface, paraAnonymousInt ->
                            ActivityCompat.requestPermissions(this@MainActivity,
                                arrayOf(REQUEST_PERMISSION),
                                REQUEST_PERMISSION_CODE)
                        }
                            .setNegativeButton(getString(R.string.cancel_option)){
                                    paramAnonymousDialogInterface, paraAnonymousInt ->
                                paramAnonymousDialogInterface.dismiss()
                                finish()
                            }
                        localBuilder.show()
                    }else{
                        ActivityCompat.requestPermissions(this, arrayOf(REQUEST_PERMISSION),REQUEST_PERMISSION_CODE)
                    }
                    return true
                }
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            if (requestCode == REQUEST_PERMISSION_CODE){
                if (grantResults != null && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    setLayout()
                }else{
                    iUtils.ShowToast(this@MainActivity, getString(R.string.info_permission_denined))
                    finish()
                }
            }
        } catch (e: Exception){
            e.printStackTrace()
            iUtils.ShowToast(this@MainActivity, getString(R.string.info_permission_denined))
            finish()
        }
    }
}
