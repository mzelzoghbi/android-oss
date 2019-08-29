package com.kickstarter.ui.fragments

import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding.view.RxView
import com.kickstarter.R
import com.kickstarter.extensions.showSnackbar
import com.kickstarter.libs.BaseFragment
import com.kickstarter.libs.qualifiers.RequiresFragmentViewModel
import com.kickstarter.libs.rx.transformers.Transformers
import com.kickstarter.libs.utils.ViewUtils
import com.kickstarter.models.Project
import com.kickstarter.models.Reward
import com.kickstarter.ui.viewholders.NativeCheckoutRewardViewHolder
import com.kickstarter.viewmodels.BackingFragmentViewModel
import kotlinx.android.synthetic.main.fragment_backing.*
import kotlinx.android.synthetic.main.fragment_backing_section_summary_total.*
import kotlinx.android.synthetic.main.fragment_pledge_section_summary_pledge.*
import kotlinx.android.synthetic.main.fragment_pledge_section_summary_shipping.*
import kotlinx.android.synthetic.main.item_reward.*
import kotlinx.android.synthetic.main.reward_card_details.*

@RequiresFragmentViewModel(BackingFragmentViewModel.ViewModel::class)
class BackingFragment: BaseFragment<BackingFragmentViewModel.ViewModel>()  {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_backing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.viewModel.outputs.showUpdatePledgeSuccess()
                .compose(bindToLifecycle())
                .compose(Transformers.observeForUI())
                .subscribe { showSnackbar(view, getString(R.string.Got_it_your_changes_have_been_saved)) }

        this.viewModel.outputs.cardLogo()
                .compose(bindToLifecycle())
                .compose(Transformers.observeForUI())
                .subscribe { reward_card_logo.setImageResource(it) }

        this.viewModel.outputs.cardExpiration()
                .compose(bindToLifecycle())
                .compose(Transformers.observeForUI())
                .subscribe { setCardExpirationText(it) }

        this.viewModel.outputs.cardLastFour()
                .compose(bindToLifecycle())
                .compose(Transformers.observeForUI())
                .subscribe { setCardLastFourText(it) }

        this.viewModel.outputs.cardIsGone()
                .compose(bindToLifecycle())
                .compose(Transformers.observeForUI())
                .subscribe { ViewUtils.setGone(card_details, it) }

        this.viewModel.outputs.projectAndReward()
                .compose(bindToLifecycle())
                .compose(Transformers.observeForUI())
                .subscribe { bindDataToRewardViewHolder(it) }

        this.viewModel.outputs.backerNumber()
                .compose(bindToLifecycle())
                .compose(Transformers.observeForUI())
                .subscribe { setBackerNumberText(it) }

        this.viewModel.outputs.pledgeDate()
                .compose(bindToLifecycle())
                .compose(Transformers.observeForUI())
                .subscribe { setPledgeDateText(it) }

        this.viewModel.outputs.pledgeAmount()
                .compose(bindToLifecycle())
                .compose(Transformers.observeForUI())
                .subscribe { pledge_summary_amount.text = it }

        this.viewModel.outputs.shippingAmount()
                .compose(bindToLifecycle())
                .compose(Transformers.observeForUI())
                .subscribe { shipping_summary_amount.text = it }

        this.viewModel.outputs.shippingSummaryIsGone()
                .compose(bindToLifecycle())
                .compose(Transformers.observeForUI())
                .subscribe { ViewUtils.setGone(shipping_summary, it) }

        this.viewModel.outputs.totalAmount()
                .compose(bindToLifecycle())
                .compose(Transformers.observeForUI())
                .subscribe { total_summary_amount.text = it }

        this.viewModel.outputs.receivedSectionIsGone()
                .compose(bindToLifecycle())
                .compose(Transformers.observeForUI())
                .subscribe { ViewUtils.setGone(received_section, it) }

        this.viewModel.outputs.receivedCheckboxChecked()
                .compose(bindToLifecycle())
                .compose(Transformers.observeForUI())
                .subscribe { mark_as_received_checkbox.isChecked = it }

        RxView.clicks(mark_as_received_checkbox)
                .compose(bindToLifecycle())
                .subscribe { this.viewModel.inputs.receivedCheckboxToggled(mark_as_received_checkbox.isChecked) }
    }

    fun takeProject(project: Project) {
        this.viewModel.inputs.project(project)
    }

    fun pledgeSuccessfullyCancelled() {
        this.viewModel.inputs.pledgeSuccessfullyUpdated()
    }

    private fun bindDataToRewardViewHolder(projectAndReward: Pair<Project, Reward>) {
        val rewardViewHolder = NativeCheckoutRewardViewHolder(reward_container, delegate = null, inset = true)
        val project = projectAndReward.first
        val reward = projectAndReward.second
        rewardViewHolder.bindData(Pair(project, reward))
    }

    private fun setBackerNumberText(it: String?) {
        backer_number.text = this.viewModel.ksString.format(getString(R.string.backer_modal_backer_number), "backer_number", it)
    }

    private fun setCardExpirationText(expiration: String) {
        reward_card_expiration_date.text = this.viewModel.ksString.format(getString(R.string.Credit_card_expiration),
                "expiration_date", expiration)
    }

    private fun setCardLastFourText(lastFour: String) {
        reward_card_last_four.text = this.viewModel.ksString.format(getString(R.string.Ending_in_last_four), "last_four", lastFour)
    }

    private fun setPledgeDateText(pledgeDate: String) {
        backing_date.text = this.viewModel.ksString.format(getString(R.string.As_of_pledge_date), "pledge_date", pledgeDate)
    }

}