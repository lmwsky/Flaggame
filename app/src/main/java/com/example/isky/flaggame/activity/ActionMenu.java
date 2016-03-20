package com.example.isky.flaggame.activity;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.example.isky.flaggame.R;
import com.example.isky.flaggame.game.GameConfig;
import com.example.isky.flaggame.game.GameManager;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;

/**
 * Created by isky on 2016/3/14.
 * 控制地图右下角的ActionButton以及点击之后展开的一系列按钮的类，包括设置他们的监听
 */
public class ActionMenu {
    public static final int ICON_ACTIONBAR = R.drawable.actionbar;
    public static final int ICON_ATTRACT = R.drawable.icon_attract;
    public static final int ICON_OCCUPY = R.drawable.icon_occupy;
    public static final int ICON_REBIRTH = R.drawable.icon_rebirth;
    public static final int ICON_ENDGAME = R.drawable.icon_endgame;
    private final FloatingActionButton actionButton;
    private final SubActionButton bt_attract;
    private final SubActionButton bt_occupy;
    private final SubActionButton bt_rebirth;
    private final SubActionButton bt_endgame;
    private final FloatingActionMenu actionMenu;
    private final SubActionButton.Builder itemBuilder;
    private final Resources resources;
    /*按钮菜单所在的activity*/
    private Activity activity;
    private SubActionButton bt_skill;

    /**
     * 构造一个一个悬浮在右下角的按钮以及对应菜单
     *
     * @param activity 按钮所在的activity
     */
    public ActionMenu(Activity activity) {
        this.activity = activity;
        resources = activity.getResources();
        itemBuilder = new SubActionButton.Builder(activity);

        ArrayList<SubActionButton> subActionButtonlist = new ArrayList<>();

        actionButton = produceActionButton(ICON_ACTIONBAR);

        bt_attract = produceSubActionButton(ICON_ATTRACT);
        bt_attract.setOnClickListener(new GameManager.OnAttractBtClickListener());
        subActionButtonlist.add(bt_attract);


        int skillresourceid = GameConfig.bitmap_skill;
        if (skillresourceid != 0) {
            bt_skill = produceSubActionButton(skillresourceid);
            bt_skill.setOnClickListener(new GameManager.OnSkillBtClickListener());
            subActionButtonlist.add(bt_skill);
        }

        bt_occupy = produceSubActionButton(ICON_OCCUPY);
        bt_occupy.setOnClickListener(new GameManager.OnOccupyBtClickListener());
        subActionButtonlist.add(bt_occupy);


        bt_rebirth = produceSubActionButton(ICON_REBIRTH);
        bt_rebirth.setOnClickListener(new GameManager.OnRebirthBtClickListener());
        subActionButtonlist.add(bt_rebirth);


        bt_endgame = produceSubActionButton(ICON_ENDGAME);
        bt_endgame.setOnClickListener(new GameManager.OnEndGameClickListener());
        subActionButtonlist.add(bt_endgame);


        FloatingActionMenu.Builder builder = new FloatingActionMenu.Builder(activity);
        for (SubActionButton subActionButton : subActionButtonlist)
            builder.addSubActionView(subActionButton);
        actionMenu = builder.attachTo(actionButton).build();
    }

    /**
     * 在UI的右下角添加一个悬浮圆形按钮，点击就会展开一系列弧形排列的按钮
     *
     * @return 悬浮按钮对象
     */
    public FloatingActionButton produceActionButton(int drawable_resourceid) {
        ImageView icon = new ImageView(activity);
        Drawable drawable = resources.getDrawable(drawable_resourceid);
        icon.setImageDrawable(drawable);
        return new FloatingActionButton.Builder(activity)
                .setContentView(icon)
                .build();
    }

    /**
     * 产生一个图片为对应resouceid的按钮
     *
     * @param drawable_resourceid 图片资源id
     * @return 已经初始化的子按钮
     */
    public SubActionButton produceSubActionButton(int drawable_resourceid) {
        ImageView imageView = new ImageView(activity);
        Drawable drawable = resources.getDrawable(drawable_resourceid);
        imageView.setImageDrawable(drawable);
        return itemBuilder.setContentView(imageView).build();
    }

    /**
     * 把按钮添加到悬浮菜单
     *
     * @param subActionButton 要添加的子按钮
     */
    public void attachSubButton(SubActionButton subActionButton) {
        if (actionMenu.isOpen()) {
            actionMenu.close(true);
            actionMenu.addViewToCurrentContainer(subActionButton);
            actionMenu.open(true);
        } else
            actionMenu.addViewToCurrentContainer(subActionButton);
    }

    /**
     * 把按钮从悬浮菜单移除
     *
     * @param subActionButton 要移除的子按钮
     */
    public void removeSubButton(SubActionButton subActionButton) {
        if (actionMenu.isOpen()) {
            actionMenu.close(true);
            actionMenu.removeViewFromCurrentContainer(subActionButton);
            actionMenu.open(true);
        } else
            actionMenu.removeViewFromCurrentContainer(subActionButton);
    }
}
