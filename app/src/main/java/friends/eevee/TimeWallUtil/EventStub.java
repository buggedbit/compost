package friends.eevee.TimeWallUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.Button;

public class EventStub extends Button {

    public EventStub(Context context) {
        super(context);
    }

    public EventStub(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Save the current matrix
        canvas.save();
        //Rotate this View at its center
        canvas.rotate(UIPreferences.EVENT_STUB.ANGLE_OF_TEXT, this.getWidth() / 2, this.getHeight() / 2);
        //Draw it
        super.onDraw(canvas);
        //Restore to the previous matrix
        canvas.restore();
    }
}
