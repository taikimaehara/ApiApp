package jp.techacademy.taiki.maehara.apiapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_web_view.*
import kotlinx.android.synthetic.main.recycler_favorite.*
import java.util.*

class WebViewActivity : AppCompatActivity() {

    private val viewPagerAdapter by lazy { ViewPagerAdapter(this) }

    private var favoriteShop: FavoriteShop = FavoriteShop()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        setSupportActionBar(findViewById(R.id.webView_toolbar))

        favoriteShop = intent.getSerializableExtra(KEY_URL) as? FavoriteShop ?: return { finish() }()
        webView.loadUrl(favoriteShop.url)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_web_view, menu)

        val actionFavoriteItem = menu?.findItem(R.id.action_favorite)

        //お気に入り状態に応じて、アイコンを更新する。
        updateFavoriteItem(actionFavoriteItem)

        return super.onCreateOptionsMenu(menu)
    }

    private fun updateFavoriteItem(actionFavoriteItem: MenuItem?) {
        if(FavoriteShop.findBy(favoriteShop.id) != null){
            actionFavoriteItem?.setIcon(R.drawable.ic_star)
        } else {
            actionFavoriteItem?.setIcon(R.drawable.ic_star_border)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_favorite -> {
            if(FavoriteShop.findBy(favoriteShop.id) != null){
                showConfirmDeleteFavoriteDialog(item)
            } else {
                addFavorite(item)
            }
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun addFavorite(item: MenuItem) {
        FavoriteShop.insert(FavoriteShop().apply {
            id = favoriteShop.id
            name = favoriteShop.name
            address = favoriteShop.address
            imageUrl = favoriteShop.imageUrl
            url = favoriteShop.url
        })
//        (viewPagerAdapter.fragments[MainActivity.VIEW_PAGER_POSITION_API] as ApiFragment).updateView()
//        (viewPagerAdapter.fragments[MainActivity.VIEW_PAGER_POSITION_FAVORITE] as FavoriteFragment).updateData()
        updateFavoriteItem(item)
    }

    private fun showConfirmDeleteFavoriteDialog(item: MenuItem) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_favorite_dialog_title)
            .setMessage(R.string.delete_favorite_dialog_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                deleteFavorite(item)
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->}
            .create()
            .show()
    }

    private fun deleteFavorite(item: MenuItem) {
        FavoriteShop.delete(favoriteShop.id)
//        favoriteShop.flag = Calendar.getInstance().time
//        (viewPagerAdapter.fragments[MainActivity.VIEW_PAGER_POSITION_API] as ApiFragment).updateView()
//        (viewPagerAdapter.fragments[MainActivity.VIEW_PAGER_POSITION_FAVORITE] as FavoriteFragment).updateData()
        updateFavoriteItem(item)
    }

    companion object {
        private const val KEY_URL = "key_url"
        fun start(activity: Activity, favoriteShop: FavoriteShop) {
            activity.startActivity(Intent(activity, WebViewActivity::class.java).putExtra(KEY_URL, favoriteShop))
        }

    }

}