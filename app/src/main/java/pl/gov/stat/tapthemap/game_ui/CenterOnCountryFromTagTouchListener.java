package pl.gov.stat.tapthemap.game_ui;

import android.view.View;

import pl.gov.stat.tapthemap.Country;
import pl.gov.stat.tapthemap.scene.MapRenderer;

public class CenterOnCountryFromTagTouchListener implements View.OnClickListener {

    private final MapRenderer renderer;

    public CenterOnCountryFromTagTouchListener(MapRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void onClick(View view) {
        if (view.getTag() == null) {
            return;
        }
        Country country = (Country) view.getTag();
        renderer.centerViewAt(country);
    }
}
