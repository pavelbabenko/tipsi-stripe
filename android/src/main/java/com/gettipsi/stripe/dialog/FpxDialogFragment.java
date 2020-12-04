package com.gettipsi.stripe.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.facebook.react.bridge.Promise;
import com.gettipsi.stripe.R;
import com.gettipsi.stripe.StripeModule;
import com.gettipsi.stripe.util.CardFlipAnimator;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.view.AddPaymentMethodActivityStarter;

import org.jetbrains.annotations.NotNull;

/**
 * Created by dmitriy on 11/13/16
 */

public class FpxDialogFragment extends DialogFragment {

  public static final String ERROR_CODE = "errorCode";
  public static final String ERROR_DESCRIPTION = "errorDescription";

  private String errorCode;
  private String errorDescription;

  private ProgressBar progressBar;
  private Button selectPaymentButton;

  private volatile Promise promise;
  private boolean successful;
  private CardFlipAnimator cardFlipAnimator;
  private Button doneButton;

  public static FpxDialogFragment newInstance(
    final String errorCode,
    final String errorDescription
  ) {
    Bundle args = new Bundle();
    args.putString(ERROR_CODE, errorCode);
    args.putString(ERROR_DESCRIPTION, errorDescription);

    FpxDialogFragment fragment = new FpxDialogFragment();
    fragment.setArguments(args);
    return fragment;
  }


  public void setPromise(Promise promise) {
    this.promise = promise;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle arguments = getArguments();
    if (arguments != null) {
      errorCode = arguments.getString(ERROR_CODE);
      errorDescription = arguments.getString(ERROR_DESCRIPTION);
    }
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final View view = View.inflate(getActivity(), R.layout.payment_form_fragment_stripe_fpx, null);
    final AlertDialog dialog = new AlertDialog.Builder(getActivity())
      .setView(view)
      .setTitle(R.string.gettipsi_card_enter_dialog_title)
      .setPositiveButton(R.string.gettipsi_card_enter_dialog_positive_button, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
          onSaveCLick();
        }
      })
      .setNegativeButton(R.string.gettipsi_card_enter_dialog_negative_button, null).create();
    dialog.show();

    doneButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
    doneButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onSaveCLick();
      }
    });
    doneButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
    doneButton.setEnabled(false);

    bindViews(view);
    init();

    return dialog;
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    if (!successful && promise != null) {
      promise.reject(errorCode, errorDescription);
      promise = null;
    }
    super.onDismiss(dialog);
  }

  private void bindViews(final View view) {
    progressBar = (ProgressBar) view.findViewById(R.id.buttonProgress);
    selectPaymentButton = (Button) view.findViewById(R.id.select_payment_method_button);
  }


  private void init() {
    selectPaymentButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        launchAddPaymentMethod();
      }
    });
    successful = false;
  }

  private void launchAddPaymentMethod() {
    new AddPaymentMethodActivityStarter(this)
      .startForResult(new AddPaymentMethodActivityStarter.Args.Builder()
        .setPaymentMethodType(PaymentMethod.Type.Fpx)
        .build()
      );
  }

  public void onSaveCLick() {
    doneButton.setEnabled(false);
    progressBar.setVisibility(View.VISIBLE);


  }

}
