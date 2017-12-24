package com.system.mconcentrator.mconcentrator.scrollablepanel;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kelin.scrollablepanel.library.PanelAdapter;
import com.system.mconcentrator.mconcentrator.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kelin on 16-11-18.
 */

public class ScrollablePanelAdapter extends PanelAdapter {
    private static final int TITLE_TYPE = 4;
    private static final int ROOM_TYPE = 0;
    private static final int DATE_TYPE = 1;
    private static final int ORDER_TYPE = 2;

    private List<RoomInfo> roomInfoList = new ArrayList<>();
    private List<DateInfo> dateInfoList = new ArrayList<>();
    private List<List<OrderInfo>> ordersList = new ArrayList<>();

    private TableNumClickListener TableNumListener = null;

    //自定义接口，用于回调点击事件到到CopyMeter188
    public interface TableNumClickListener {
        public void tableNumclickListener(int row, int column, List<List<OrderInfo>> ordersList, OrderViewHolder viewHolder);
    }

    public void setOnTableNumClickListener(TableNumClickListener TableNum) {
        TableNumListener = TableNum;
    }

    @Override
    public int getRowCount() {
        return roomInfoList.size() + 1;
    }

    @Override
    public int getColumnCount() {
        return dateInfoList.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int row, int column) {
        int viewType = getItemViewType(row, column);
        switch (viewType) {
            case DATE_TYPE:
                setDateView(column, (DateViewHolder) holder);
                break;
            case ROOM_TYPE:
                setRoomView(row, (RoomViewHolder) holder);
                break;
            case ORDER_TYPE:
                setOrderView(row, column, (OrderViewHolder) holder, false, null);
                break;
            case TITLE_TYPE:
                break;
            default:
                setOrderView(row, column, (OrderViewHolder) holder, false, null);
        }
    }

    public int getItemViewType(int row, int column) {
        if (column == 0 && row == 0) {
            return TITLE_TYPE;
        }
        if (column == 0) {
            return ROOM_TYPE;
        }
        if (row == 0) {
            return DATE_TYPE;
        }
        return ORDER_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case DATE_TYPE:
                return new DateViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listitem_date_info, parent, false));
            case ROOM_TYPE:
                return new RoomViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listitem_room_info, parent, false));
            case ORDER_TYPE:
                return new OrderViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listitem_order_info, parent, false));
            case TITLE_TYPE:
                return new TitleViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listitem_title, parent, false));
            default:
                break;
        }
        return new OrderViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_order_info, parent, false));
    }


    private void setDateView(int pos, DateViewHolder viewHolder) {
        DateInfo dateInfo = dateInfoList.get(pos);
        if (dateInfo != null && pos > 0) {
            viewHolder.dateTextView.setText(dateInfo.getDate());
            //viewHolder.weekTextView.setText(dateInfo.getWeek());
        }
    }

    private void setRoomView(int pos, RoomViewHolder viewHolder) {
        RoomInfo roomInfo = roomInfoList.get(pos - 1);
        if (roomInfo != null && pos > 0) {
            // viewHolder.roomTypeTextView.setText(roomInfo.getRoomType());
            viewHolder.roomNameTextView.setText(roomInfo.getRoomName());
        }
    }

    public void setOrderView(final int row, final int column, final OrderViewHolder viewHolder, boolean isFixed, final OrderInfo orderInfo1) {

        if (isFixed) {
            //属于回调函数情况下，更新ordersList数据源
            ordersList.get(row-1).set(column-1, orderInfo1);
        }
        {
            final OrderInfo orderInfo = ordersList.get(row-1).get(column-1);

            if (orderInfo != null) {
                viewHolder.nameTextView.setText(orderInfo.getGuestName());
                // if (orderInfo.getStatus() != OrderInfo.Status.BLANK)
                {
                    viewHolder.itemView.setClickable(true);
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {//&& column == 5
                            if (column == 5) {
//                            LogHelper.d("row",row+"");
//                            LogHelper.d("column",column+"");
                                String orderString = orderInfo.getGuestName();
                                Toast.makeText(v.getContext(), "name1:" + orderInfo.getGuestName(), Toast.LENGTH_SHORT).show();
                                TableNumListener.tableNumclickListener(row, column, ordersList, viewHolder);
                            } else {
                                int i = 2;
                                while (column - i >= 0 && ordersList.get(row - 1).get(column - i).getId() == orderInfo.getId()) {
                                    i++;
                                }
                                final OrderInfo info = ordersList.get(row - 1).get(column - i + 1);
                                Toast.makeText(v.getContext(), "name2:" + info.getGuestName(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
//                else {
//                    viewHolder.itemView.setClickable(false);
//                }
            }
        }
    }

    public void setOrderViewOrderinfo(final int row, final int column, OrderInfo orderInfo, OrderViewHolder viewHolder) {
        //属于回调函数情况下，更新ordersList数据源
        ordersList.get(column - 1).set(row - 1, orderInfo);
        if (orderInfo != null) {
            viewHolder.nameTextView.setText(orderInfo.getGuestName());
        }
    }

    private static class DateViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public TextView weekTextView;

        public DateViewHolder(View itemView) {
            super(itemView);
            this.dateTextView = (TextView) itemView.findViewById(R.id.date);
            //this.weekTextView = (TextView) itemView.findViewById(R.id.week);
        }

    }

    private static class RoomViewHolder extends RecyclerView.ViewHolder {
        // public TextView roomTypeTextView;
        public TextView roomNameTextView;

        public RoomViewHolder(View view) {
            super(view);
            // this.roomTypeTextView = (TextView) view.findViewById(R.id.room_type);
            this.roomNameTextView = (TextView) view.findViewById(R.id.room_name);
        }
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView statusTextView;
        public View view;

        public OrderViewHolder(View view) {
            super(view);
            this.view = view;
            // this.statusTextView = (TextView) view.findViewById(R.id.status);
            this.nameTextView = (TextView) view.findViewById(R.id.guest_name);
        }
    }

    private static class TitleViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;

        public TitleViewHolder(View view) {
            super(view);
            this.titleTextView = (TextView) view.findViewById(R.id.title);
        }
    }


    public void setRoomInfoList(List<RoomInfo> roomInfoList) {
        this.roomInfoList = roomInfoList;
    }

    public void setDateInfoList(List<DateInfo> dateInfoList) {
        this.dateInfoList = dateInfoList;
    }

    public void setOrdersList(List<List<OrderInfo>> ordersList) {
        this.ordersList = ordersList;
    }
}
