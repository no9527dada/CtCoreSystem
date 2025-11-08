//变速  游戏速度  游戏调速 游戏变速
// 已在JAVA中实现，无需重复实现
/*
Events.on(EventType.ClientLoadEvent, e => {
    let first = true;
    Vars.ui.settings.game.sliderPref(Core.bundle.format("9527xiao"), 100, 100, 10000, 1000, */
/*默认,最小,最大,每次多少*//*
i => {
        if (first) {
            first = false;
            return;
        };
        let s = i / 100;
        Time.setDeltaProvider(() => Math.min(Core.graphics.getDeltaTime() * 60 * s, 3 * s));
        return i / 100 + "X";
    });
});
*/
