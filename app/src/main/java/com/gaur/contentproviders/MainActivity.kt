package com.gaur.contentproviders

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gaur.contentproviders.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding:ActivityMainBinding?=null
    private val binding:ActivityMainBinding
    get() = _binding!!


    private val registerActivityForResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permission ->
            if(permission){
                getContactList(context = this)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val set = getContactList(this)

        binding.rvContacts.adapter = ContactAdapter(set.toList())

    }




    @SuppressLint("Recycle", "Range")
    fun getContactList(context: Context) : MutableSet<String>{
        val set = mutableSetOf<String>()
        sdkIntAboveOreo {
            isPermissionGranted(context,android.Manifest.permission.READ_CONTACTS){
                if(it){
                    val resolver = context.contentResolver
                    val cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, arrayOf<String>(), null, null)
                    if (cursor?.moveToFirst() == true) {
                        do {
                            val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                            set.add(name)
                            Log.d("TAG", "getContactList: $name")
                        } while (cursor.moveToNext())
                    }
                }else{
                    registerActivityForResult.launch(android.Manifest.permission.READ_CONTACTS)
                }
            }
        }
        return set
    }
}



inline fun sdkIntAboveOreo(call: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        call()
    }
}
inline fun isPermissionGranted(context:Context,permission:String,call:(Boolean)->Unit){
    if(ContextCompat.checkSelfPermission(context,permission) == PackageManager.PERMISSION_GRANTED){
        call.invoke(true)
    }else{
        call.invoke(false)
    }
}

