package com.example.anle.demoggmap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import static android.support.v7.widget.helper.ItemTouchHelper.*;

enum ButtonsState{ //Định nghĩ tập hợp các hằng số mục đích để check trạng thái Button
    GONE,
    VISIBLE
}

public class SwipeController extends ItemTouchHelper.Callback { //extend một lớp hỗ trọ thao tác vuốt cho RecyclerView

    private boolean swipeBack = false; //Kiểm tra cho phép chúng ta vuốt lại lần nữa hay không
    private ButtonsState buttonShowedState = ButtonsState.GONE; //Ban đầu thì Button có trạng thái là GONE
    private static final float buttonWidth = 300; // Chiều rộng của Button
    private RectF buttonXoa = null; //Dùng để chứa buttonXoa ta đã vẽ trong drawButton()
    private RectF buttonSua=null; //Dùng để chứa buttonSua ta đã vẽ trong drawButton()
    private RecyclerView.ViewHolder currentItemViewHolder = null; //Chứa View của Item hiện tại ta vuốt
    private SwipeControllerAction buttonsActions=null; //Một đối tượng của lớp Abstract SwipeControllerAction quản lý hành động nhấn Button chúng ta tạo ra

    private static final String TAG=SwipeController.class.getSimpleName();

    public SwipeController(SwipeControllerAction buttonsActions){
        this.buttonsActions=buttonsActions;
    }

    @Override
    //Phương thức bên dưới cho biết loại hành động nào đối với Item trong RecyclerView sẽ được xử lý
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        Log.d(TAG,"getMovementFlags");
        return makeMovementFlags(0,LEFT); //Cho phép vuốt sang trái
    }

    @Override
    //Phương thức bên dưới Được gọi khi ItemTouchHelper muốn di chuyển Item đã kéo từ vị trí cũ sang vị trí mới.
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        Log.d(TAG,"onMove");
        return false;
    }

    @Override
    //Phương thức bên dưới Được gọi khi ViewHolder bị người dùng vuốt.
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Log.d(TAG,"onSwiped");
    }

    @Override
    //Phương thức dùng để chặn không cho vuốt bay mất Item :V Thử comment phương thức này lại và vuốt thử thì biết
    //Ban đầu thì swipeBack=false và chúng ta vuốt Item thì nó chạy vào Phương thức này và return về 1028, khi chúng ta thả tay ra thì nó cũng chạy vào phương thức này
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        Log.d(TAG,"convertToAbsoluteDirection: "+layoutDirection);
        if (swipeBack){ //swipeBack ở đây đóng vai trò có cho chúng ta vuốt item hay không và ngăn chặn Item bay ra, khi chúng ta thả tay ngừng vuốt thì swipeBack sẽ bằng true trả Item chúng ta về vị trí ban đầu
            swipeBack = buttonShowedState != ButtonsState.GONE; //Nếu Button trạng thái chưa VISIBLE thì swipeBack vẫn bằng false để có thể vuốt item tiếp nữa, bởi vì khi chúng ta thả tay ra là swipeBack đã là true rồi
            //nên khi đó chúng ta vuốt lần nữa nó sẽ vẫn chạy vào đây và return 0 làm chúng ta không thể vuốt nữa.
            Log.d(TAG,"convertToAbsoluteDirection: return 0 swipeBack=true");
            return 0; //return về một giá trị kiểu int 0 và kết thúc phương thức //Điều này ngăn không cho nó bay ra khỏi màn hình và không cho phép chúng ta vuốt sang trái
            //**Khi vuốt đến một mức độ nào đó thì nếu không có return 0 này thì nó sẽ bay luôn về phía bên trái không còn thấy trên màn hình nữa
        }
        Log.d(TAG,"convertToAbsoluteDirection: return super. swipeBack=false: "+super.convertToAbsoluteDirection(flags, layoutDirection));
        return super.convertToAbsoluteDirection(flags,layoutDirection); //Cái này return về 1028 cho phép chúng ta vuốt sang trái và có thể bay ra khỏi màn hình,
        //tùy hướng chúng ta quy định ở trên getMovementFlags() sẽ return về giá trị khác nhau
    }

    @Override
    //Phương thức bên dưới Được gọi khi ta vuốt Item và thả tay ra trên đường quay về vị trí cũ trước khi vuốt cũng sẽ gọi phương thức này luôn
    //Dùng để thay đổi giao diện vẽ lại View đối với tương tác của người dùng (cụ thể ở đây là Item(row) trong RecyclerView)
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        Log.d(TAG,"onChildDraw");
        if (actionState==ACTION_STATE_SWIPE){ //Nếu thao tác là vuốt
            Log.d(TAG,"onChildDraw: actionState==ACTION_STATE_SWIPE");
            if (buttonShowedState == ButtonsState.VISIBLE){ //Nếu đã kéo đủ tới mức trạng thái của button chuyển sang VISIBLE (điều này được kiểm tra trong phương thức setTouchListener()
                //Khi ta vuốt sang trái thì tọa độ dX sẽ dần dần giảm
                //Lưu ý khi vuốt và thả tay ra thì Item sẽ quay về vị trí ban đầu, trên đường quay về tọa độ X sẽ thay đổi (lớn dần)
                //nên ở đây ta sẽ lấy dX nhỏ nhất để cố định Item không quay về vị trí ban đầu và nằm bên trái các Button chúng ta sẽ không bị che nữa
                dX = Math.min(dX, -buttonWidth*2);
                Log.d(TAG,"onChildDraw: Right_Visible dX="+dX);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            }else { //Nếu không thì sẽ gọi phương thức setTouchListener() nằm vùng chờ đến ngày ButtonsState=Visible :V
                Log.d(TAG,"onChildDraw: buttonShowedState==ButtonsState.GONE: setTouchListener()");
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                Log.d(TAG,"onChildDraw: dX="+dX);
            }
        }

        if (buttonShowedState == ButtonsState.GONE) {
            Log.d(TAG,"onChildDraw: buttonShowedState==ButtonsState.GONE: super.onChildDraw");
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        currentItemViewHolder = viewHolder; //Item hiện tại vừa vuốt, truyền cái này cho drawButton để vẽ button ngay tại Item đó
    }

    //Phương thức bên dưới sẽ thực hiện đến phương thức vẽ button nếu vừa vuốt một Item và currentItemViewHolder được gán là Item đó
    public void onDraw(Canvas c) {
        Log.d(TAG,"onDraw");
        if (currentItemViewHolder != null) {
            Log.d(TAG,"onDraw: drawButtons");
            drawButtons(c, currentItemViewHolder);
        }
    }

    //Phương thức bên dưới xử lý sự kiện khi nhấn xuống Item
    private void setTouchDownListener(final Canvas c,
                                      final RecyclerView recyclerView,
                                      final RecyclerView.ViewHolder viewHolder,
                                      final float dX, final float dY,
                                      final int actionState, final boolean isCurrentlyActive) {
        Log.d(TAG,"setTouchDownListener");
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) { //Khi nhấn xuống RecyclerView sẽ gọi tới setTouchUpListener để lắng nghe
                                                                    //sự kiện thả tay ra
                    Log.d(TAG,"setTouchDownListener: event.getAction() == MotionEvent.ACTION_DOWN: setTouchUpListener");
                    setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
                return false;
            }
        });
    }

    private void setTouchUpListener(final Canvas c,
                                    final RecyclerView recyclerView,
                                    final RecyclerView.ViewHolder viewHolder,
                                    final float dX, final float dY,
                                    final int actionState, final boolean isCurrentlyActive) {
        Log.d(TAG,"setTouchUpListener");
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) { //Khi người dùng thả tay ra không nhấn nữa trên RecyclerView
                    Log.d(TAG,"setTouchUpListener: event.getAction() == MotionEvent.ACTION_UP: SwipeController.super.onChildDraw(dX=0F)");
                    //"Duy nhất một" câu lệnh bên dưới trả về vị trí cũ cho Item với dX=0
                    SwipeController.super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);

                    setItemsClickable(recyclerView, true); //Lúc này các Item có thể click và nhận sự kiện onClick()
                    swipeBack = false; //Cho phép chúng ta có thể vuốt lại nữa

                    //contains kiểm tra khi ta thả tay ra thì nơi ta thả tay ra là buttonSua sẽ thực hiện sự kiện sửa
                    if (buttonsActions!=null&&buttonSua!=null&&buttonSua.contains(event.getX(),event.getY())){
                        //Gọi tới phương thức sửa được định nghĩ làm những gì bên Actitivy khởi tạo đối tượng SwipeController
                        buttonsActions.onSuaClicked(viewHolder.getAdapterPosition());
                    }
                    //contains kiểm tra khi ta thả tay ra thì nơi ta thả tay ra là buttonXoa sẽ thực hiện sự kiện xóa
                    if (buttonsActions!=null&&buttonXoa!=null&&buttonXoa.contains(event.getX(),event.getY())){
                        //Gọi tới phương thức xóa được định nghĩ làm những gì bên Actitivy khởi tạo đối tượng SwipeController
                        buttonsActions.onXoaClicked(viewHolder.getAdapterPosition());
                    }
                    buttonShowedState = ButtonsState.GONE; //Thiết lập lại Button ẩn đi
                    currentItemViewHolder = null;

                }
                return false;
            }
        });

    }

    //Phương thức bên dưới lắng nghe thao tác vuốt
    private void setTouchListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive){
        Log.d(TAG,"setTouchListener");
        recyclerView.setOnTouchListener(new View.OnTouchListener() { //Đăng ký sự kiện onTouch cho RecyclerView chúng ta truyền vào
            @Override
            public boolean onTouch(View v, MotionEvent event) { //Sử lý khi sự kiện onTouch xảy ra
                Log.d(TAG,"setTouchListener: Action: "+event);
                //Nếu vuốt và thả ra theo event.getAction() rơi vào hai trường hợp ACTION_CANCEL hoặc ACTION_UP thì swipeBack=true
                swipeBack=event.getAction()==MotionEvent.ACTION_CANCEL||event.getAction()==MotionEvent.ACTION_UP;

                if (swipeBack){
                    //2 câu if bên dưới có giải thích bằng hình
                    if (dX<-buttonWidth*2) { // Nếu kéo Item qua bên trái trên trục tọa độ X làm tâm của nó < số âm chiều rộng của 2 Button
                        Log.d(TAG, "setTouchListener: dX= " + dX);
                        buttonShowedState = ButtonsState.VISIBLE; //Trạng thái Button lúc này sẽ hiện ra phía bên phải Item
                    }

                    if (buttonShowedState!=ButtonsState.GONE){ //Nếu trạng thái của các nút không còn ẩn nữa mà đã hiển thị thì
                        Log.d(TAG,"setTouchListener: buttonShowedState!=ButtonsState.GONE: setTouchDownListener");
                        //Gọi phương thức xử lý khi nhấn vào Item để khi nhấn vào và thả ra một lần nữa thì Button sẽ ẩn đi và Item về vị trí ban đầu
                        setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        setItemsClickable(recyclerView,false); //Không cho click vào những View đang hiện trong RecyclerView để tránh gọi sự kiện onClick của những Item
                    }
                }

                return false;
            }
        });
    }

    //Phương thức bên dưới set Item đó có thể Click vào được không
    private void setItemsClickable(RecyclerView recyclerView,boolean isClickable){
        Log.d(TAG,"setItemsClickable: recyclerView.getChildCount()= "+recyclerView.getChildCount());
        //recyclerView.getChildCount() lấy ra số lượng View con đã được tạo hiện đang hiển thị trên RecyclerView trong ViewGroup
        for (int k=0;k<recyclerView.getChildCount();++k){
            recyclerView.getChildAt(k).setClickable(isClickable); //Có cho phép View đó được click vào hay không
        }
    }

    //Phương thức bên dưới tạo ra một Button
    private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder) {
        Log.d(TAG,"drawButtons");
        float buttonWidthWithoutPadding = buttonWidth - 20;
        float corners = 16; //Thông số bo góc

        View itemView = viewHolder.itemView; //Lấy ra Item(row) trong RecyclerView để vẽ button tại vị trí của nó
        Paint p = new Paint();

        //RectF vẽ một hình chữ nhật với các cạch của nó theo trục tọa độ X,Y. VD: Tham số đầu tiên truyền vào là
        //cạnh trái của nó getRight là lấy điểm ngoài cùng bên trái X lớn nhất của itemView và trừ vào một khoảng để
        //cạnh bên trái được tạo ra tại vị trí cách viền phải của itemView một khoảng bằng khoảng trừ
        //các tham số còn lại lần lượt là top,right,bottom tương ứng với các cạnh của hình chữ nhật
        RectF buttonSua = new RectF(itemView.getRight() - (buttonWidthWithoutPadding*2+20), itemView.getTop(), itemView.getRight()-(buttonWidthWithoutPadding+20), itemView.getBottom());
        p.setColor(Color.GREEN); //Màu vẽ button là màu đỏ
        c.drawRoundRect(buttonSua, corners, corners, p); //Bo tròn góc cho button
        drawText("SỬA", c, buttonSua, p);

        RectF buttonXoa = new RectF(itemView.getRight() - buttonWidthWithoutPadding, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        p.setColor(Color.RED);
        c.drawRoundRect(buttonXoa, corners, corners, p); //Bo tròn góc cho button
        drawText("XÓA", c, buttonXoa, p);

        this.buttonXoa = null;
        this.buttonSua=null;
        if (buttonShowedState == ButtonsState.VISIBLE) {
            this.buttonXoa=buttonXoa;
            this.buttonSua=buttonSua;
        }
    }

    private void drawText(String text, Canvas c, RectF button, Paint p) { //Phương thức tạo nhãn cho button
        float textSize = 50;
        p.setColor(Color.WHITE); //Màu chữ
        p.setAntiAlias(true); //Làm mịn chữ
        p.setTextSize(textSize); //Thiết lập cỡ chữ

        float textWidth = p.measureText(text); //Trả về chiều rộng của chữ ta thiết lập cho nút
        //Vẽ văn bản(tạo ra text) cho button với các tham số text , x , y , paint
        c.drawText(text, button.centerX()-(textWidth/2), button.centerY()+(textSize/2), p);
    }
}
