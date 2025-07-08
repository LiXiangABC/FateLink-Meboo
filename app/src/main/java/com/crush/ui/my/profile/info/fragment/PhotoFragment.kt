package com.crush.ui.my.profile.info.fragment

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.crush.R
import com.crush.mvp.MVPBaseFragment

class PhotoFragment : MVPBaseFragment<PhotoContract.View, PhotoPresenter>(), PhotoContract.View {


    override fun bindLayout(): Int {
        return R.layout.layout_profile_photo
    }

    override val photoList: RecyclerView?
        get() = view?.findViewById(R.id.photo_list)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mPresenter?.onActivityResult(requestCode, resultCode, data)
    }
}