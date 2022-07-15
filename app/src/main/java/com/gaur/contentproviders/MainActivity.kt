package com.gaur.contentproviders

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.loader.app.LoaderManager
import androidx.loader.app.LoaderManager.LoaderCallbacks
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.gaur.contentproviders.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() , LoaderCallbacks<Cursor>{

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!

    private val list:MutableLiveData<MutableSet<String>> = MutableLiveData()


     val registerActivityForResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
               list.postValue(getContactList())
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        list.postValue(getContactList())

        list.observe(this){
            it?.let {
                binding.rvContacts.adapter = MainAdapter(it.toList())
            }
        }
    }


    @SuppressLint("Range", "Recycle")
    fun getContactList():MutableSet<String>{
        val set = mutableSetOf<String>()
        sdkIntAboveOreo {
            isPermissionGranted(this,android.Manifest.permission.READ_CONTACTS){
                if(it){

                    LoaderManager.getInstance(this).initLoader(0,null,this)

                }else{
                    registerActivityForResult.launch(android.Manifest.permission.READ_CONTACTS)
                }
            }
        }
        return set
    }


    inline fun sdkIntAboveOreo(call: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            call.invoke()
        }
    }

    inline fun isPermissionGranted(context: Context, permission: String, call: (Boolean) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            call.invoke(true)
        } else {
            call.invoke(false)
        }

    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        Log.d("TAG", "onCreateLoader: ${Thread.currentThread().name}")
     return CursorLoader(this,ContactsContract.Contacts.CONTENT_URI,null,null,null,null)
    }

    @SuppressLint("Range")
    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {
        val set = mutableSetOf<String>()
         if(cursor?.moveToFirst()==true){
            do {
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                set.add(name)
            }while(cursor.moveToNext())
        }
        list.postValue(set)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

    }
}





