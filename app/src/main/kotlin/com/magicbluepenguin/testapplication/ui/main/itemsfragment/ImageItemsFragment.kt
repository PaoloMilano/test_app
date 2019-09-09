package com.magicbluepenguin.testapplication.ui.main.itemsfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.magicbluepenguin.testapplication.R
import com.magicbluepenguin.testapplication.TestApplication
import com.magicbluepenguin.testapplication.databinding.ImageListItemFragmentBinding
import com.magicbluepenguin.testapplication.ui.main.itemsfragment.Adapter.ImageItemsRecyclerViewAdapter
import com.magicbluepenguin.testapplication.ui.main.itemsfragment.viewmodel.ImageItemsViewModel
import com.magicbluepenguin.testapplication.util.GenericNetworkError
import com.magicbluepenguin.testapplication.util.NetworkError
import com.magicbluepenguin.testapplication.util.NetworkUnavailableError
import com.magicbluepenguin.testapplication.util.UnsecureConnectionError

class ImageItemsFragment : Fragment() {

    companion object {
        fun newInstance() =
            ImageItemsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.image_list_item_fragment,
            container,
            false
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        with(activity as? AppCompatActivity) {
            this?.let {

                val dataBinding = DataBindingUtil.setContentView<ImageListItemFragmentBinding>(
                    this,
                    R.layout.image_list_item_fragment
                ).apply {
                    lifecycleOwner = viewLifecycleOwner
                    itemsListView.boundAdapter = ImageItemsRecyclerViewAdapter()
                }

                val appDatabase = (application as? TestApplication)?.run { appDatabase }
                    ?: throw IllegalStateException("AppDatabase is not available")

                ImageItemsViewModel.getInstanceWithCahedRepository(
                    this,
                    appDatabase.imageItemDao(),
                    "463154134a6642d51c714d685ec0efcb",
                    "sha256/rCCCPxtKvFVDrKOPDSfirp4bQOYw4mIVKn8fZxgQcs4="
                ).apply {
                    connectToDataStream {
                        dataBinding.imageItems = it
                    }
                    dataBinding.itemsViewModel = this
                }.networkError.observe(viewLifecycleOwner,
                    Observer<NetworkError> { error ->
                        (activity?.findViewById(android.R.id.content) as View).run {
                            val activityContext = this.context
                            val ignore = when (error!!) {
                                is GenericNetworkError -> showSnackBar(R.string.generic_network_error)
                                is NetworkUnavailableError -> showSnackBar(R.string.network_unavailable_error)
                                is UnsecureConnectionError -> {
                                    AlertDialog.Builder(activityContext)
                                        .setCancelable(false)
                                        .setMessage(R.string.network_unsecure_error)
                                        .setPositiveButton(R.string.close) { _, _ -> finish() }
                                        .create().show()
                                }
                            }
                        }
                    })
            }
        }
    }

    private fun showSnackBar(@StringRes stringRes: Int, duration: Int? = Snackbar.LENGTH_SHORT) {
        activity?.findViewById<View>(android.R.id.content)?.run {
            Snackbar.make(this, stringRes, duration!!).show()
        }
    }
}
