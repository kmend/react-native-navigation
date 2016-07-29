package com.reactnativenavigation.layouts;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.reactnativenavigation.params.ActivityParams;
import com.reactnativenavigation.params.ScreenParams;
import com.reactnativenavigation.params.TitleBarButtonParams;
import com.reactnativenavigation.params.TitleBarLeftButtonParams;
import com.reactnativenavigation.screens.ScreenStack;
import com.reactnativenavigation.views.BottomTabs;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class BottomTabsLayout extends RelativeLayout implements Layout, AHBottomNavigation.OnTabSelectedListener {

    private final AppCompatActivity activity;
    private ActivityParams params;
    private BottomTabs bottomTabs;
    private ScreenStack[] screenStacks;
    private int currentStackIndex = 0;

    public BottomTabsLayout(AppCompatActivity activity, ActivityParams params) {
        super(activity);
        this.activity = activity;
        this.params = params;
        screenStacks = new ScreenStack[(params.tabParams.size())];
        createLayout();
    }

    private void createLayout() {
        createBottomTabs();
        addBottomTabsToScreen();
        addScreenStacks();
        showInitialScreenStack();
    }

    private void addScreenStacks() {
        for (int i = 0; i < screenStacks.length; i++) {
            createAndAddScreenStack(i);
        }
    }

    private void createAndAddScreenStack(int position) {
        ScreenStack newStack = new ScreenStack(activity, params.tabParams.get(position));
        screenStacks[position] = newStack;
        newStack.setVisibility(INVISIBLE);
        addView(newStack, 0, new LayoutParams(MATCH_PARENT, MATCH_PARENT));
    }

    private void createBottomTabs() {
        bottomTabs = new BottomTabs(getContext());
        bottomTabs.addTabs(params.tabParams, this);
    }

    private void addBottomTabsToScreen() {
        LayoutParams lp = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        lp.addRule(ALIGN_PARENT_BOTTOM);
        addView(bottomTabs, lp);
    }

    private void showInitialScreenStack() {
        showStack(screenStacks[0]);
    }

    @Override
    public View asView() {
        return this;
    }

    @Override
    public boolean onBackPressed() {
        if (getCurrentScreenStack().canPop()) {
            getCurrentScreenStack().pop();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setTopBarVisible(String screenInstanceId, boolean hidden, boolean animated) {
        for (int i = 0; i < bottomTabs.getItemsCount(); i++) {
            screenStacks[i].setTopBarVisible(screenInstanceId, hidden, animated);
        }
    }

    @Override
    public void setTitleBarTitle(String screenInstanceId, String title) {
        for (int i = 0; i < bottomTabs.getItemsCount(); i++) {
            screenStacks[i].setTitleBarTitle(screenInstanceId, title);
        }
    }

    @Override
    public void setTitleBarRightButtons(String screenInstanceId, String navigatorEventId, List<TitleBarButtonParams> titleBarButtons) {
        for (int i = 0; i < bottomTabs.getItemsCount(); i++) {
            screenStacks[i].setTitleBarRightButtons(screenInstanceId, navigatorEventId, titleBarButtons);
        }
    }

    @Override
    public void setTitleBarLeftButton(String screenInstanceId, String navigatorEventId, TitleBarLeftButtonParams titleBarLeftButtonParams) {
        for (int i = 0; i < bottomTabs.getItemsCount(); i++) {
            screenStacks[i].setTitleBarLeftButton(screenInstanceId, navigatorEventId, titleBarLeftButtonParams);
        }
    }

    @Override
    public void push(ScreenParams screenParams) {
        getCurrentScreenStack().push(screenParams);
    }

    @Override
    public void pop(ScreenParams screenParams) {
        getCurrentScreenStack().pop();
    }

    @Override
    public void popToRoot(ScreenParams params) {
        getCurrentScreenStack().popToRoot();
    }

    @Override
    public void newStack(ScreenParams params) {
        ScreenStack currentScreenStack = getCurrentScreenStack();
        currentScreenStack.destroy();
        removeView(currentScreenStack);

        ScreenStack newStack = new ScreenStack(activity, params);
        screenStacks[currentStackIndex] = newStack;
        addView(newStack, 0, new RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
    }

    @Override
    public void destroy() {
        for (ScreenStack screenStack : screenStacks) {
            screenStack.destroy();
        }
    }

    @Override
    public boolean onTabSelected(int position, boolean wasSelected) {
        hideCurrentStack();

        ScreenStack newStack = screenStacks[position];
        showStack(newStack);
        currentStackIndex = position;

        return true;
    }

    private void showStack(ScreenStack newStack) {
        newStack.setVisibility(VISIBLE);
        bottomTabs.setStyleFromScreen(newStack.getCurrentScreenStyleParams());
    }

    private void hideCurrentStack() {
        ScreenStack currentScreenStack = getCurrentScreenStack();
        currentScreenStack.setVisibility(INVISIBLE);
    }

    private ScreenStack getCurrentScreenStack() {
        return screenStacks[currentStackIndex];
    }
}