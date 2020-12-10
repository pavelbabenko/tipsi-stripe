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
import com.facebook.react.bridge.Promise;
import com.gettipsi.stripe.R;
import com.gettipsi.stripe.StripeModule;
import com.gettipsi.stripe.util.CardFlipAnimator;
import com.gettipsi.stripe.util.Converters;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputListener;
import com.stripe.android.view.CardInputWidget;
import com.stripe.android.view.CardInputListener.FocusField.Companion;

import org.jetbrains.annotations.NotNull;

/**
 * Created by dmitriy on 11/13/16
 */

public class AddCardDialogFragmentV2 extends DialogFragment {

  public static final String ERROR_CODE = "errorCode";
  public static final String ERROR_DESCRIPTION = "errorDescription";

  private String errorCode;
  private String errorDescription;

  private ProgressBar progressBar;
  private ImageView imageFlipedCard;
  private ImageView imageFlipedCardBack;
  private CardInputWidget cardInputWidget;

  private volatile Promise promise;
  private boolean successful;
  private CardFlipAnimator cardFlipAnimator;
  private Button doneButton;

  public static AddCardDialogFragmentV2 newInstance(
    final String errorCode,
    final String errorDescription
  ) {
    Bundle args = new Bundle();
    args.putString(ERROR_CODE, errorCode);
    args.putString(ERROR_DESCRIPTION, errorDescription);

    AddCardDialogFragmentV2 fragment = new AddCardDialogFragmentV2();
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
    final View view = View.inflate(getActivity(), R.layout.payment_form_fragment_stripe_element, null);
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
    doneButton.setAlpha(0.30f);
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
    cardInputWidget = (CardInputWidget) view.findViewById((R.id.card_input_widget));

    imageFlipedCard = (ImageView) view.findViewById(R.id.imageFlippedCard);
    imageFlipedCardBack = (ImageView) view.findViewById(R.id.imageFlippedCardBack);
  }


  private void init() {
    cardInputWidget.setPostalCodeEnabled(false);
    cardInputWidget.setCardInputListener(new CardInputListener() {
      @Override
      public void onFocusChange(@NotNull String s) {
        if (s == Companion.FOCUS_CVC) {
          cardFlipAnimator.showBack();
        } else if (cardFlipAnimator.getState() == "back") {
          cardFlipAnimator.showFront();
        }
      }

      @Override
      public void onCardComplete() {
        // unused
      }

      @Override
      public void onExpirationComplete() {
        // unused
      }

      @Override
      public void onCvcComplete() {
        doneButton.setEnabled(true);
        doneButton.setAlpha(1f);
      }
    });

    cardFlipAnimator = new CardFlipAnimator(getActivity(), imageFlipedCard, imageFlipedCardBack);
    successful = false;
  }

  public void onSaveCLick() {
    doneButton.setEnabled(false);
    progressBar.setVisibility(View.VISIBLE);

    PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
    if (params != null) {
      StripeModule.getInstance().getStripe().createPaymentMethod(
        params,
        new ApiResultCallback<PaymentMethod>() {

          @Override
          public void onError(Exception error) {
            doneButton.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            showToast(error.getLocalizedMessage());
          }

          @Override
          public void onSuccess(PaymentMethod paymentMethod) {
            if (promise != null) {
              promise.resolve(Converters.convertPaymentMethodToWritableMap(paymentMethod));
              promise = null;
              successful = true;
              dismiss();
            }
          }
        });
    } else {
      doneButton.setEnabled(true);
      progressBar.setVisibility(View.GONE);
      // showToast(errorMessage);
    }

  }

  public void showToast(String message) {
    Context context = getActivity();
    if (context != null && !TextUtils.isEmpty(message)) {
      Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
  }
}
