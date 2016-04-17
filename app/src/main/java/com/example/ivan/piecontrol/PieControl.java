package com.example.ivan.piecontrol;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivan on 1/18/16.
 */
public class PieControl extends View {
    private final int SECTION_MARGING = 5;
    private int outerRadius;
    private Point center;
    private int margin = 0;
    private int boundedReg = 100;
    private Paint paint = new Paint();
    private List<Section> sections = new ArrayList<>();
    private int sectionsCount;
    private int backgroundColor = Color.parseColor("#91908A");
    private int checkedBackgroundColor = Color.parseColor("#D5D4CA");
    private int alpha = 255;
    private ArrayList<SectionParameters> sectionsParams;
    private OnCheckedChangeListener onCheckedChangeListener = null;
    private int checkedId = -1;


    class SectionGeometry {
        public Path path;
        public Point[] vertices;
        public Point center;

        public SectionGeometry(Path path, Point[] vertices, Point center) {
            this.path = path;
            this.vertices = vertices;
            this.center = center;
        }

        public boolean isInside(Point point) {
            //Checking for 2 sections case
            if(vertices[1].x == vertices[2].x) {
                RectF rectF = new RectF();
                path.computeBounds(rectF, true);
                Region r = new Region();
                r.setPath(path, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
                if(r.contains(point.x, point.y)) {
                    return true;
                }
                return false;
            }
            int a = (vertices[0].x - point.x) * (vertices[1].y - vertices[0].y)
                    - (vertices[1].x - vertices[0].x) * (vertices[0].y - point.y);

            int b = (vertices[1].x - point.x) * (vertices[2].y - vertices[1].y)
                    - (vertices[2].x - vertices[1].x) * (vertices[1].y - point.y);

            int c = (vertices[2].x - point.x) * (vertices[0].y - vertices[2].y)
                    - (vertices[0].x - vertices[2].x) * (vertices[2].y - point.y);

            if ((a >= 0 && b >= 0 && c >= 0) || (a <= 0 && b <= 0 && c <= 0)){
                return true;
            }
            return false;
        }
    }

    class Section{
        private SectionGeometry sg;
        private int backgroundColor;
        private int checkedBackgroundColor;
        private int alpha = 0;
        private Bitmap icon = null;
        private Bitmap checkedIcon = null;
        private String title = null;
        private Paint fontPaint;
        private int titleColor = Color.parseColor("#FFFFFF");
        private int fontSize = 50;
        private Rect fontBounds = new Rect();
        private int id;
        private boolean isChecked = false;

        public Section( int id){
            this.id = id;
            fontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            fontPaint.setTextSize(fontSize);
            fontPaint.setStyle(Paint.Style.STROKE);
        }

        public void setGeometry(SectionGeometry sg){
            this.sg = sg;
        }

        public boolean isInside(Point point){
            return sg.isInside(point);
        }

        public void setColor(int color){
            this.backgroundColor = color;
        }

        public void setCheckedColor(int color){
            this.checkedBackgroundColor = color;
        }

        public void setAlpha(int alpha){
            this.alpha = alpha;
        }

        public void setTitle(String title, int size, int color){
            this.title = title;
            if(size > 0) {
                this.fontSize = size;
            }
            if(color >= 0) {
                this.titleColor = color;
            }
            fontPaint.getTextBounds(title, 0, title.length(), fontBounds);
        }

        public void setIcon(int resId,int checkedResId, Context context, int w, int h) {
            if(resId != 0) {
                icon = BitmapFactory.decodeResource(context.getResources(), resId);
                int width = icon.getWidth() < w ? icon.getWidth() : w;
                int height = icon.getHeight() < h ? icon.getHeight() : h;
                icon = Bitmap.createScaledBitmap(icon, width, height, true);
            }

            if(checkedResId != 0) {
                checkedIcon = BitmapFactory.decodeResource(context.getResources(), checkedResId);
                int width = checkedIcon.getWidth() < w ? checkedIcon.getWidth() : w;
                int height = checkedIcon.getHeight() < h ? checkedIcon.getHeight() : h;
                checkedIcon = Bitmap.createScaledBitmap(checkedIcon, width, height, true);
            }
        }

        public void draw(Canvas c, Paint p) {
            Bitmap i = icon;

            if(!isChecked) {
                p.setColor(backgroundColor);
            }else{
                if(checkedIcon != null) {
                    i = checkedIcon;
                }
                p.setColor(checkedBackgroundColor);
            }

            p.setAlpha(alpha);
            c.drawPath(sg.path, p);

            p.setAlpha(255);
            if(i != null && title == null) {
                c.drawBitmap(i, sg.center.x - (i.getWidth() / 2), sg.center.y - (i.getHeight() / 2), p);
            }else if(title != null){
                fontPaint.setColor(titleColor);
                c.drawText(title, sg.center.x - (fontBounds.width() / 2),  sg.center.y +
                        (fontBounds.height() / 2), fontPaint);

            }
        }

        public int getId(){
            return id;
        }

        public boolean isChecked(){
            return isChecked;
        }

        public void setChecked(boolean flag){
            isChecked = flag;
        }
    }

    public static class SectionParameters {
        public int resId = 0;
        public int resCheckedId = 0;
        public int backgroundColor = -1;
        public int checkedBackgroundColor = -1;
        public String title;
        public int titleColor = -1;
        public int titleSize;

        public SectionParameters(int resId) {
            this.resId = resId;
        }

        public SectionParameters(int resId, int resCheckedId) {
            this.resId = resId;
            this.resCheckedId = resCheckedId;
        }

        public SectionParameters(int resId, int resCheckedId, int backgroundColor, int checkedBackgroundColor) {
            this.resId = resId;
            this.resCheckedId = resCheckedId;
            this.backgroundColor = backgroundColor;
            this.checkedBackgroundColor = checkedBackgroundColor;
        }

        public SectionParameters(String title, int backgroundColor, int checkedBackgroundColor) {
            this.title = title;
            this.backgroundColor = backgroundColor;
            this.checkedBackgroundColor = checkedBackgroundColor;
        }

        public SectionParameters(String title, int size, int backgroundColor, int checkedBackgroundColor) {
            this.title = title;
            this.titleSize = size;
            this.backgroundColor = backgroundColor;
            this.checkedBackgroundColor = checkedBackgroundColor;
        }

        public SectionParameters(String title, int color, int size, int backgroundColor, int checkedBackgroundColor) {
            this.title = title;
            this.titleColor = color;
            this.titleSize = size;
            this.backgroundColor = backgroundColor;
            this.checkedBackgroundColor = checkedBackgroundColor;
        }
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(int id);
    }

    public PieControl(Context context) {
        super(context);
        init(context, null);
    }

    public PieControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if(attrs != null) {
            TypedArray typedArr = context.obtainStyledAttributes(attrs, R.styleable.pie_attr, 0, 0);
            sectionsCount = typedArr.getInt(R.styleable.pie_attr_sections_count, 0);
            backgroundColor = typedArr.getColor(R.styleable.pie_attr_active_background_color, Color.parseColor("#91908A"));
            checkedBackgroundColor = typedArr.getColor(R.styleable.pie_attr_checked_background_color, Color.parseColor("#D5D4CA"));
            alpha = typedArr.getInt(R.styleable.pie_attr_alpha, 255);
            margin = typedArr.getInt(R.styleable.pie_attr_margin, 0);
            typedArr.recycle();
        }
        initControlSections();
    }

    public void setSectiondsParams(ArrayList<SectionParameters> sp){
        sectionsParams = sp;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        onCheckedChangeListener = listener;
    }

    public void setChecked(int id) {
        for (Section s : sections) {
            if(s.getId() == id){
                checkedId = id;
                s.setChecked(true);
            }else {
                s.setChecked(false);
            }
        }
    }

    @Override
    public void onDraw(Canvas c){
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        for(Section s : sections){
            s.draw(c, paint);
        }
    }

    @Override
    public void onSizeChanged(int nw, int nh, int ow, int oh) {
        super.onSizeChanged(nw, nh, ow, oh);
        center = new Point(nw / 2, nh / 2);
        outerRadius = ((nw > nh ? nh : nw) / 2) - margin * 2;
        updateControl();
    }

    private void updateControl(){
        initControlSectionsGeometry();
        applyControlSectionsParams();
    }

    private void initControlSectionsGeometry(){
        double startAngle = 0, endAngle = 0;
        double pdeg = 360 / sectionsCount;
        endAngle = pdeg;
        double sectionsPadding = Math.asin(SECTION_MARGING / (double)outerRadius);
        sectionsPadding *= 2;
        boundedReg = (int)(Math.sin(Math.toRadians(pdeg / 2.0)) * outerRadius);
        boundedReg -= boundedReg / 5;
        for(int i = 0; i < sectionsCount; ++i){
            SectionGeometry s =  makeSectionGeometry(center.x, center.y, outerRadius,
                    SECTION_MARGING, startAngle + sectionsPadding, endAngle - sectionsPadding);
            sections.get(i).setGeometry(s);
            startAngle += pdeg;
            endAngle   += pdeg;
        }
    }

    private void applyControlSectionsParams(){
        if(sectionsParams != null) {
            for (int i = 0; i < sectionsCount && i < sectionsParams.size(); ++i) {
                Section s = sections.get(i);
                SectionParameters sp = sectionsParams.get(i);
                if(sp.title != null) {
                    s.setTitle(sp.title, sp.titleSize, sp.titleColor);
                }
                s.setIcon(sp.resId, sp.resCheckedId, getContext(), boundedReg, boundedReg);
                s.setAlpha(alpha);

                if (sp.backgroundColor >= 0) {
                    s.setColor(getResources().getColor(sp.backgroundColor));
                }else{
                    s.setColor(backgroundColor);
                }
                if(sp.checkedBackgroundColor >= 0) {
                    s.setCheckedColor(getResources().getColor(sp.checkedBackgroundColor));
                }else{
                    s.setCheckedColor(checkedBackgroundColor);
                }
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, checkedId);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        checkedId = savedState.getId();
        setChecked(checkedId);
    }

    protected static class SavedState extends BaseSavedState {
        private int id;

        private SavedState(Parcelable superState, int id) {
            super(superState);
            this.id = id;
        }

        private SavedState(Parcel in) {
            super(in);
            id = in.readInt();
        }

        public int getId() {
            return id;
        }

        @Override
        public void writeToParcel(Parcel destination, int flags) {
            super.writeToParcel(destination, flags);
            destination.writeInt(id);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                processTouchDownEvent(x, y);
                break;
            case MotionEvent.ACTION_UP:
                processTouchUpEvent(x, y);
                break;
        }

        super.onTouchEvent(event);
        invalidate();
        return true;
    }

    private void processTouchDownEvent(float x, float y){
        Point p = new Point((int)x, (int)y);
        if(isPointInsideControl(p)){
            for (Section s : sections) {
                if (s.isInside(p)) {
                    s.setChecked(true);
                    checkedId = s.getId();
                } else {
                    s.setChecked(false);
                }
            }
        }
    }

    private void processTouchUpEvent(float x, float y) {
        Point p = new Point((int)x, (int)y);
        if(isPointInsideControl(p)) {
            for (Section s : sections) {
                if (s.isInside(p) && s.isChecked()) {
                    if(onCheckedChangeListener != null) {
                        onCheckedChangeListener.onCheckedChanged(s.getId());
                    }
                }
            }
        }
    }

    private boolean isPointInsideControl(Point p) {
        double dx = p.x - center.x;
        double dy = p.y - center.y;
        return dx * dx + dy * dy <= outerRadius *outerRadius;
    }

    private void initControlSections() {
        for(int i = 0; i < sectionsCount; ++i) {
            sections.add(new Section(i));
        }
    }

    private SectionGeometry makeSectionGeometry(int xCenter,int yCenter,int radiusOuter,
                                                int radiusInner, double startAngle, double endAngle) {
        double x = 0;
        double y = 0;
        Path path = new Path();
        Point[] vertices = new Point[3];
        Point center = new Point();

        vertices[0] = new Point(xCenter, yCenter);

        x = radiusOuter * 2 * Math.sin(Math.toRadians(startAngle));
        y = radiusOuter * 2 * Math.cos(Math.toRadians(startAngle));
        vertices[1] = new Point((int)(x + xCenter), (int)(y + yCenter));

        x = radiusOuter * Math.sin(Math.toRadians(startAngle));
        y = radiusOuter * Math.cos(Math.toRadians(startAngle));
        path.moveTo((float)(x + xCenter), (float) (y + yCenter));

        for(double i = startAngle - 1; i < endAngle; ++i){
            x = radiusOuter * Math.sin(Math.toRadians(i));
            y = radiusOuter * Math.cos(Math.toRadians(i));
            path.lineTo((float)(x + xCenter), (float)(y + yCenter));
        }

        x = radiusOuter * 2 * Math.sin(Math.toRadians(endAngle));
        y = radiusOuter * 2 * Math.cos(Math.toRadians(endAngle));
        vertices[2] = new Point((int)(x + xCenter), (int)(y + yCenter));

        double midAngle = (endAngle - startAngle) / 2;

        midAngle+=startAngle;

        x = radiusInner * Math.sin(Math.toRadians(midAngle));
        y = radiusInner * Math.cos(Math.toRadians(midAngle));
        path.lineTo((float) (x + xCenter), (float) (y + yCenter));

        center.x = xCenter + (int)((((radiusOuter - radiusInner) / 2) + radiusInner)
                * Math.sin(Math.toRadians((startAngle + endAngle) / 2)));

        center.y = yCenter + (int)((((radiusOuter - radiusInner) / 2) + radiusInner)
                * Math.cos(Math.toRadians((startAngle + endAngle) / 2)));

        return new SectionGeometry(path, vertices, center);
    }

}
