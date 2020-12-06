package com.gettipsi.stripe;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.stripe.android.view.CardInputListener;
import com.stripe.android.view.CardMultilineWidget;

import org.jetbrains.annotations.NotNull;
import org.xmlpull.v1.XmlPullParser;


/**
 * Created by dmitriy on 11/15/16
 */

public class CustomCardInputReactManager extends  SimpleViewManager<CardMultilineWidget> {

  public static final String REACT_CLASS = "TPSCardField";
  private static final String TAG = CustomCardInputReactManager.class.getSimpleName();
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

  @Override
  public String getName() {
    return REACT_CLASS;
  }

  @Override
  protected CardMultilineWidget createViewInstance(final ThemedReactContext reactContext) {

    XmlPullParser parser = reactContext.getResources().getXml(R.xml.stub_element);
    try {
      parser.next();
      parser.nextTag();
    } catch (Exception e) {
      e.printStackTrace();
    }


    AttributeSet attr = Xml.asAttributeSet(parser);

    final CardMultilineWidget cardInputWidget = new CardMultilineWidget(reactContext, attr);


    setListeners(cardInputWidget);

    this.reactContext = reactContext;
    // cardInputWidget.post(new Runnable() {
    //   @Override
    //   public void run() {
    //     InputMethodManager inputMethodManager = (InputMethodManager) reactContext.getSystemService(reactContext.INPUT_METHOD_SERVICE);
    //     inputMethodManager.toggleSoftInputFromWindow(cardInputWidget.getApplicationWindowToken(), InputMethodManager.SHOW_IMPLICIT, 0);
    //     // cardInputWidget.requestFocus();
    //   }
    // });
    return cardInputWidget;
  }

  @ReactProp(name = "enabled")
  public void setEnabled(CardMultilineWidget view, boolean enabled) {
    view.setEnabled(enabled);
  }

  @ReactProp(name = "backgroundColor")
  public void setBackgroundColor(CardMultilineWidget view, int color) {
    Log.d("TAG", "setBackgroundColor: " + color);
    view.setBackgroundColor(color);
  }

  @ReactProp(name = "cardNumber")
  public void setCardNumber(CardMultilineWidget view, String cardNumber) {
    view.setCardNumber(cardNumber);
  }

  @ReactProp(name = "expDate")
  public void setExpDate(CardMultilineWidget view, String expDate) {
    view.setExpiryDate(view.getCard().getExpMonth(), view.getCard().getExpYear());
    /*  view.setExpDate(expDate, true); */
  }

  @ReactProp(name = "securityCode")
  public void setSecurityCode(CardMultilineWidget view, String securityCode) {
    view.setCvcCode(securityCode);
  }

  @ReactProp(name = "numberPlaceholder")
  public void setCreditCardTextHint(CardMultilineWidget view, String creditCardTextHint) {
    // view.setCardHint(creditCardTextHint);
    // view.setCreditCardTextHint(creditCardTextHint);
  }

  @ReactProp(name = "expirationPlaceholder")
  public void setExpDateTextHint(CardMultilineWidget view, String expDateTextHint) {
    // view.setCardHint(expDateTextHint);
    // view.setExpDateTextHint(expDateTextHint);
  }

  @ReactProp(name = "cvcPlaceholder")
  public void setSecurityCodeTextHint(CardMultilineWidget view, String securityCodeTextHint) {
    // view.setCardHint(securityCodeTextHint);
    // view.setSecurityCodeTextHint(securityCodeTextHint);
  }


  private void setListeners(final CardMultilineWidget view) {


    view.setCardInputListener(new CardInputListener() {
      @Override
      public void onFocusChange(@NotNull FocusField focusField) {
        Toast.makeText(reactContext, focusField.toString(), Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onCardComplete() {
        Toast.makeText(reactContext, "Card completed", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onExpirationComplete() {
        Toast.makeText(reactContext, "Expiration completed", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onCvcComplete() {
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
      .dispatchEvent(new CreditCardFormOnChangeEvent(view.getId(), currentParams, true));
  }

  private void updateView(CardMultilineWidget view) {
  }
}





