package com.utkarshxgupta.snapnotifmanager;

import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.MaterialColors;
import com.utkarshxgupta.snapnotifmanager.Adapter.WhitelistAdapter;
import com.utkarshxgupta.snapnotifmanager.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class RecylerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private WhitelistAdapter adapter;

    public RecylerItemTouchHelper(WhitelistAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder targer) {
        return false;
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAbsoluteAdapterPosition();
        if (direction==ItemTouchHelper.LEFT) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(adapter.getContext());
            builder.setTitle("Delete Person");
            builder.setMessage("Are you sure you want to delete this person?");
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.deleteItem(position);
                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    adapter.notifyItemChanged(viewHolder.getAbsoluteAdapterPosition());
                }
            });
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    adapter.notifyItemChanged(viewHolder.getAbsoluteAdapterPosition());
                }
            });

            builder.create().show();
        }
        else {
            adapter.editItem(position);
        }
    }
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        Drawable icon;
        View itemView = viewHolder.itemView;

        // Create a rounded rectangle path for the background
        float cornerRadius = 46;
        Path path = new Path();
        path.addRoundRect(new RectF(itemView.getLeft(), itemView.getTop(), itemView.getRight(), itemView.getBottom()), cornerRadius, cornerRadius, Path.Direction.CW);

        // Set the background color of the item view and draw the rounded rectangle over it
        Paint p = new Paint();

        if(dX>0) {
            p.setColor(Color.rgb(98, 165, 204));
            c.drawPath(path, p);
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.edit);
        }
        else {
            p.setColor(Color.rgb(227, 96, 86));
            c.drawPath(path, p);
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.trash);
        }

        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight())/2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight())/2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if (dX>0) { //right Swipe
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

        }
        else if (dX<0) { //Left Swipe
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        }
        icon.draw(c);
    }
}
