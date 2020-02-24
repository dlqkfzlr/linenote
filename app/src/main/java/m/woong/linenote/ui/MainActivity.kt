package m.woong.linenote.ui

import android.Manifest
import android.annotation.TargetApi
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import m.woong.linenote.R
import m.woong.linenote.utils.toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //퍼미션 상태 확인
            if (!hasPermissions(PERMISSIONS)) {

                //퍼미션 허가 안되어있다면 사용자에게 요청
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE)
            } else {
                //퍼미션 허가됨
            }
        }
        val navController = Navigation.findNavController(this, R.id.fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            Navigation.findNavController(this, R.id.fragment),
            null
        )
    }

    /*
   여기서부턴 퍼미션 관련 메소드
    */
    internal val PERMISSIONS_REQUEST_CODE = 1000
    internal var PERMISSIONS =
        arrayOf("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE")


    private fun hasPermissions(permissions: Array<String>): Boolean {
        var result: Int

        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (perms in permissions) {

            result = ContextCompat.checkSelfPermission(this, perms)

            if (result == PackageManager.PERMISSION_DENIED) {
                //허가 안된 퍼미션 발견
                return false
            }
        }

        //모든 퍼미션이 허가되었음
        return true
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {

            PERMISSIONS_REQUEST_CODE -> if (grantResults.size > 0) {
                val cameraPermissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                if (!cameraPermissionAccepted) {
                    showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.")
                } else {
                    //퍼미션 허가됨
                }
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private fun showDialogForPermission(msg: String) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("알림")
        builder.setMessage(msg)
        builder.setCancelable(false)
        builder.setPositiveButton(
            "예"
        ) { dialog, id -> requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE) }
        builder.setNegativeButton(
            "아니오"
        ) { arg0, arg1 -> finish() }
        builder.create().show()
    }
}
