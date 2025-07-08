package com.crush.ui.index.match

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.crush.R
import com.crush.util.AnimUtil
import com.crush.util.SoftInputUtils
import com.crush.view.CircleImageView
import com.custom.base.config.BaseConfig
import com.crush.mvp.MVPBaseActivity
import io.rong.imkit.SpName


class MatchUserActivity : MVPBaseActivity<MatchUserContract.View, MatchUserPresenter>(), MatchUserContract.View {

    override fun setFullScreen(): Boolean {
        return true
    }
    override fun bindLayout(): Int {
        return R.layout.act_match_user
    }

    override fun initView() {
        imgMatchIconOne.isVisible=BaseConfig.getInstance.getInt(SpName.trafficSource,0)!=1
//        imgMatchIconTwo.isVisible=BaseConfig.getInstance.getInt(SpName.trafficSource,0)!=1
        imgMatchIconThree.isVisible=BaseConfig.getInstance.getInt(SpName.trafficSource,0)!=1
        imgMatchIconFour.isVisible=BaseConfig.getInstance.getInt(SpName.trafficSource,0)!=1

        Handler().postDelayed(Runnable {
            containerBackArrow.visibility= View.VISIBLE

        },70)

        Handler().postDelayed(Runnable {
            editUserMatch.isFocusable=true
//           SoftInputUtils.showSoftInput(editUserMatch)

        },700)
        containerBackArrow.setOnClickListener {
            onBackPressed()
        }

        emojiWave.text =  String(Character.toChars(0x1F44B))
        emojiWave.setOnClickListener {
            AnimUtil().shake(emojiWave)
            Handler().postDelayed(Runnable {
                mPresenter?.tryToSend(emojiWave.text.toString())
            },500)
        }
        emojiWinkingFace.text =  String(Character.toChars(0x1F609))
        emojiWinkingFace.setOnClickListener {
            AnimUtil().shake(emojiWinkingFace)
            Handler().postDelayed(Runnable {
                mPresenter?.tryToSend(emojiWinkingFace.text.toString())
            },500)
        }
        emojiHeart.text =  String(Character.toChars(0x2764))
        emojiHeart.setOnClickListener {
            AnimUtil().shake(emojiHeart)

            Handler().postDelayed(Runnable {
                mPresenter?.tryToSend(emojiHeart.text.toString())
            },500)
        }
        emojiLecherous.text =  String(Character.toChars(0x1F60D))
        emojiLecherous.setOnClickListener {
            AnimUtil().shake(emojiLecherous)
            Handler().postDelayed(Runnable {
                mPresenter?.tryToSend(emojiLecherous.text.toString())
            },500)
        }
        editUserMatch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                txtMatchSend.isEnabled = editUserMatch.text.isNotEmpty()

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        txtMatchSend.setOnClickListener {
            mPresenter?.tryToSend(editUserMatch.text.toString())

        }

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val l = intArrayOf(0, 0)
                v.getLocationInWindow(l)
                onTouchEditText(ev.x > l[0]
                        && ev.x < l[0] + v.getWidth()
                        && ev.y > l[1]
                        && ev.y < l[1] + v.getHeight())
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun onTouchEditText(isTouchInEditText: Boolean) {
        if(!isTouchInEditText){
            SoftInputUtils.hideSoftInput(editUserMatch)
        }
    }



    override val imgUserMatch: CircleImageView
        get() = findViewById(R.id.img_user_match)
    override val imgUserMatchRight: CircleImageView
        get() = findViewById(R.id.img_user_match_right)
    override val containerBackArrow: ImageView
        get() = findViewById(R.id.container_back_arrow)
    override val emojiWave: TextView
        get() = findViewById(R.id.emoji_wave)
    override val emojiWinkingFace: TextView
        get() = findViewById(R.id.emoji_winking_face)
    override val emojiHeart: TextView
        get() = findViewById(R.id.emoji_heart)
    override val emojiLecherous: TextView
        get() = findViewById(R.id.emoji_lecherous)
    override val editUserMatch: EditText
        get() = findViewById(R.id.edit_user_match)

    override val txtMatchSend: TextView
        get() = findViewById(R.id.txt_match_send)
    override val editContainer: ConstraintLayout
        get() = findViewById(R.id.out_edit_container)
    override val imgMatchIconOne: ImageView
        get() = findViewById(R.id.img_match_icon_one)
    override val imgMatchIconTwo: ImageView
        get() = findViewById(R.id.img_match_icon_two)
    override val imgMatchIconThree: ImageView
        get() = findViewById(R.id.img_match_icon_three)
    override val imgMatchIconFour: ImageView
        get() = findViewById(R.id.img_match_icon_four)

}