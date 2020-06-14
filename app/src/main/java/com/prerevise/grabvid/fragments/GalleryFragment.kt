package com.prerevise.grabvid.fragments

import android.content.Context
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prerevise.grabvid.R
import com.prerevise.grabvid.models.ModelVideo
import com.prerevise.grabvid.utils.Constants
import com.prerevise.grabvid.utils.Constants.DOWNLOAD_DIRECTORY
import kotlinx.android.synthetic.main.fragment_gallery.view.*
import java.io.File

class GalleryFragment: Fragment() {

    var recyclerViewGallery: RecyclerView? = null
    var recyclerViewLayoutManager: RecyclerView.LayoutManager? = null
    var al_video = ArrayList<ModelVideo>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_gallery, container, false)

        recyclerViewGallery = view.galleryRecyclerView
        recyclerViewLayoutManager = GridLayoutManager(context!!,3)
        recyclerViewGallery!!.layoutManager = recyclerViewLayoutManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
           videoAndroidQ(context!!, requireActivity(), true)
        }else{
            videoAndroid(context!!, requireActivity(), true)
        }

        return view
    }

    fun videoAndroid(cn: Context, activity: FragmentActivity, f: Boolean){
        al_video = ArrayList<ModelVideo>()
        val int_position = 0
        val uri: Uri
        val cursor: Cursor
        val column_index_data: Int
        val column_index_folder_name: Int
        val column_id: Int
        val thum: Int
        val duration: Int

        var absolutePathOfImage: String? = null
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val condition = MediaStore.Video.Media.DATA+ "like?"
        val selectionArguments = arrayOf("%$DOWNLOAD_DIRECTORY%")
        val sortOrder = MediaStore.Video.Media.DATE_TAKEN + "DESC"
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Video.Media.DURATION
        )
        cursor = cn.contentResolver.query(uri,projection,condition,selectionArguments,"$sortOrder")!!
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
        column_id = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA)
        duration = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
        var i: Int = 0
        while (cursor.moveToNext()){
            absolutePathOfImage = cursor.getString(column_index_data)

            try {
                val mp: MediaPlayer = MediaPlayer.create(
                    activity, FileProvider.getUriForFile(
                        context!!, context!!.applicationContext.packageName + ".provider",
                        File(absolutePathOfImage)
                    )
                )
                val durationnew: Int = mp.duration

                if (absolutePathOfImage.contains(Constants.MY_ANDROID_10_IDENTIFIER_OF_FILE)) {
                    val obj_model = ModelVideo()
                    obj_model.isBoolean_selected = false
                    obj_model.str_path = absolutePathOfImage
                    obj_model.str_thumb = cursor.getString(thum)
                    obj_model.duration = durationnew
                    obj_model.id = i

                    al_video.add(obj_model)

                    i = i + 1
                } else {
                    val obj_model = ModelVideo()
                    obj_model.isBoolean_selected = false
                    obj_model.str_path = absolutePathOfImage
                    obj_model.str_thumb = cursor.getString(thum)
                    obj_model.duration = durationnew
                    obj_model.id = i

                    al_video.add(obj_model)
                    i = i + 1
                }
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }

        obj_adapter = AdapterVideoFolder(cn, al_video, activity)

        recyclerViewGallery!!.adapter = null
        recyclerViewGallery!!.adapter = obj_adapter
        obj_adapter!!.notifyDataSetChanged()

    }

    fun videoAndroidQ
}