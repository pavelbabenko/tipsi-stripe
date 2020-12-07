package com.gettipsi.stripe;

import android.view.View;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.BaseViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReactCardMultilineManager extends BaseViewManager {
  public static final String REACT_CLASS = "TPSCardField";
  private static final int COMMAND_GET_PAYMENT_INTENT = 1;
  private ReactCardMultilineView reactCardMultilineView;

  @Nonnull
  @Override
  public String getName() {
    return REACT_CLASS;
  }

  @ReactProp(name = "enabled", defaultBoolean = true)
  public void setEnabled(ReactCardMultilineView view, boolean enabled) {
    view.setEnabledFromJS(enabled);
  }

  @ReactProp(name = "postalCodeEntryEnabled", defaultBoolean = true)
  public void setPostalCodeEntryEnabled(ReactCardMultilineView view, boolean enabled) {
    view.setPostalCodeEnabledFromJS(enabled);
  }

  @Override
  public Class getShadowNodeClass() {
    return ReactCardMultilineShadowNode.class;
  }


  public void getPaymentIntent(int requestId) {
    reactCardMultilineView.getPaymentIntent(requestId);
  }


  // mapping command
  @Override
  public Map<String, Integer> getCommandsMap() {
    return MapBuilder.of("getPaymentIntent", COMMAND_GET_PAYMENT_INTENT);
  }

  @Override
  public Map getExportedCustomDirectEventTypeConstants() {
    return MapBuilder.of(
      "getPaymentIntent",
      MapBuilder.of("registrationName", "onPaymentIntent")
    );
  }

  // js send command to native via this
  @Override
  public void receiveCommand(@Nonnull View root, int commandId, @Nullable ReadableArray args) {
    super.receiveCommand(root, commandId, args);
    switch (commandId) {
      case COMMAND_GET_PAYMENT_INTENT: {
        // The code you are going to execute when the command is called
        if (args != null) {
          final int requestId = args.getInt(0);
          this.getPaymentIntent(requestId);
        }


        break;
      }
      default: {
        break;
      }
    }
  }

  @Override
  public ReactCardMultilineShadowNode createShadowNodeInstance() {
    return new ReactCardMultilineShadowNode();
  }

  @Nonnull
  @Override
  protected View createViewInstance(@Nonnull ThemedReactContext reactContext) {
    reactCardMultilineView = new ReactCardMultilineView(reactContext);
    return reactCardMultilineView;
  }

  @Override
  public void updateExtraData(@Nonnull View root, Object extraData) {
    // do nothing
  }
}
