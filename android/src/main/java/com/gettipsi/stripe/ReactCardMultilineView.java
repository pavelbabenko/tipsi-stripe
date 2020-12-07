package com.gettipsi.stripe;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.gettipsi.stripe.util.Converters;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputListener;
import com.stripe.android.view.CardMultilineWidget;

import org.jetbrains.annotations.NotNull;

public class ReactCardMultilineView extends FrameLayout {
  final CardMultilineWidget cardInputWidget;
  private static final String TAG = ReactCardMultilineView.class.getSimpleName();

  private static final String NUMBER = "number";
  private static final String EXP_MONTH = "expMonth";
  private static final String EXP_YEAR = "expYear";
  private static final String CCV = "cvc";


  private ThemedReactContext reactContext;
  private WritableMap currentParams;

  private String currentNumber;
  private int currentMonth;
  private int currentYear;
  private String currentCCV;

  public ReactCardMultilineView(@NonNull ThemedReactContext context) {
    super(context);
    this.reactContext = context;
    setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    cardInputWidget = new CardMultilineWidget(context);
    cardInputWidget.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    // set this
    // there is a bug, label is not shown if this is not set
    cardInputWidget.setUsZipCodeRequired(false);


    // get card event and send to client
    setListeners(cardInputWidget);

    addView(cardInputWidget);

  }


  public void setEnabledFromJS(boolean enabled) {
    cardInputWidget.setEnabled(enabled);
  }

  public void setPostalCodeEnabledFromJS(boolean enabled) {
    cardInputWidget.setShouldShowPostalCode(enabled);
  }

  public void getPaymentIntent(final int requestId) {
    PaymentMethodCreateParams paymentMethodCreateParams = cardInputWidget.getPaymentMethodCreateParams();

    StripeModule.getInstance().getStripe().createPaymentMethod(
      paymentMethodCreateParams,
      new ApiResultCallback<PaymentMethod>() {

        @Override
        public void onError(Exception error) {
          Toast.makeText(reactContext, error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
//          doneButton.setEnabled(true);
//          progressBar.setVisibility(View.GONE);
//          showToast(error.getLocalizedMessage());
        }

        @Override
        public void onSuccess(PaymentMethod paymentMethod) {
          WritableMap event = Arguments.createMap();
          event.putInt("requestId", requestId);
          WritableMap result =Converters.convertPaymentMethodToWritableMap(paymentMethod);
          event.putMap("result", result);
          reactContext.getJSModule(RCTEventEmitter.class)
            .receiveEvent(getId(), "getPaymentIntent", event);
        }
      });
  }

  private void setListeners(final CardMultilineWidget view) {

    view.setCardInputListener(new CardInputListener() {

      @Override
      public void onFocusChange(@NotNull FocusField focusField) {
        //
      }

      @Override
      public void onCardComplete() {
        //
      }

      @Override
      public void onExpirationComplete() {
        //
      }

      @Override
      public void onCvcComplete() {
        //
      }
    });


    view.setCardNumberTextWatcher(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.d(TAG, "onTextChanged: cardNumber = " + charSequence);
        currentNumber = charSequence.toString().replaceAll(" ", "");
        postEvent(view);
      }

      @Override
      public void afterTextChanged(Editable editable) {
      }
    });

    view.setCvcNumberTextWatcher(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.d(TAG, "onTextChanged: EXP_YEAR = " + charSequence);

        try {
          currentMonth = view.getCard().getExpMonth();
        } catch (Exception e) {
          if (charSequence.length() == 0)
            currentMonth = 0;
        }
        try {
          currentYear = view.getCard().getExpYear();
        } catch (Exception e) {
          currentYear = 0;
        }
        postEvent(view);
      }

      @Override
      public void afterTextChanged(Editable editable) {
      }
    });

    view.setCvcNumberTextWatcher(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.d(TAG, "onTextChanged: CCV = " + charSequence);
        currentCCV = charSequence.toString();
        postEvent(view);
      }

      @Override
      public void afterTextChanged(Editable editable) {
      }
    });
  }


  private void postEvent(final CardMultilineWidget view) {
    currentParams = Arguments.createMap();
    currentParams.putString(NUMBER, currentNumber);
    currentParams.putInt(EXP_MONTH, currentMonth);
    currentParams.putInt(EXP_YEAR, currentYear);
    currentParams.putString(CCV, currentCCV);

    reactContext
      .getNativeModule(UIManagerModule.class)
      .getEventDispatcher()
      .dispatchEvent(new CreditCardFormOnChangeEvent(getId(), currentParams, true));
  }
}
