package com.gettipsi.stripe;

import android.view.View;

import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.yoga.YogaMeasureFunction;
import com.facebook.yoga.YogaMeasureMode;
import com.facebook.yoga.YogaMeasureOutput;
import com.facebook.yoga.YogaNode;
import com.stripe.android.view.CardMultilineWidget;

public class ReactCardMultilineShadowNode extends LayoutShadowNode implements YogaMeasureFunction {

  public ReactCardMultilineShadowNode() {
    setMeasureFunction(this);
  }

  @Override
  public long measure(YogaNode node, float width, YogaMeasureMode widthMode, float height, YogaMeasureMode heightMode) {
    CardMultilineWidget cardMultilineWidget = new CardMultilineWidget(this.getThemedContext());
    Integer spec = View.MeasureSpec.makeMeasureSpec((int) width, View.MeasureSpec.EXACTLY);
    cardMultilineWidget.measure(spec, View.MeasureSpec.UNSPECIFIED);
    return YogaMeasureOutput.make(cardMultilineWidget.getMeasuredWidth(), cardMultilineWidget.getMeasuredHeight());
  }
}
