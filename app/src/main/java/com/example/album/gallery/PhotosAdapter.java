package com.example.album.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.album.R;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int currentState;

    public List<Integer> getItemsSelected() {
        return itemsSelected;
    }

    private List<Integer> itemsSelected = new ArrayList<>();
    private boolean allSelectedFlags = false;
    private TreeMap<LocalDateTime, List<CheckBox>> mapCheckboxes = new TreeMap<>();

    public TreeMap<LocalDateTime, List<CheckBox>> getMapCheckBoxes() {
        return mapCheckboxes;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public interface AdapterCallback{
        void onItemClick(ImageViewHolder holder, ImageItem item);
        void linearItemDecoration(ImageView imageView);
        void OnItemLongClick(ImageViewHolder holder, ImageItem item);
        void OnCheckBoxClick(ImageViewHolder holder);
        void OnHeaderCheckBoxClick(HeaderViewHolder holder);
    }

    public List<ListItem> getItems() {
        return items;
    }

    private List<ListItem> items;
    private AdapterCallback listener;
    private boolean isLinearLayout = false;
    private Context context;
    ConstraintSet set;

    public void setLinearLayout(boolean linearLayout) {
        isLinearLayout = linearLayout;
    }

    public PhotosAdapter(List<ListItem> items, AdapterCallback listener) {
        this.items = items;
        this.listener = listener;
        set = new ConstraintSet();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType){
            case ListItem.TYPE_HEADER:{
                View view = inflater.inflate(R.layout.date_header,parent,false);
                return new HeaderViewHolder(view);
            }
            case ListItem.TYPE_EVENT:{
                View itemView = inflater.inflate(R.layout.image_item, parent, false);
                return new ImageViewHolder(itemView);
            }
            default:
                throw new IllegalStateException("unsupported item type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType){
            case ListItem.TYPE_HEADER:{
                HeaderItem header = (HeaderItem) items.get(position);
                HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
                viewHolder.headerText.setText(DateUtils.formatDate(header.getDate()));

                viewHolder.dateTime = header.getDate();
                List<CheckBox> checkBoxes = mapCheckboxes.computeIfAbsent(viewHolder.dateTime.truncatedTo(ChronoUnit.DAYS), k -> new ArrayList<>());
                checkBoxes.add(viewHolder.checkBox);

                if(currentState == PhotosFragment.CHANGED_MODE){
                    viewHolder.checkBox.setVisibility(View.VISIBLE);
                }
                viewHolder.checkBox.setOnClickListener(v -> listener.OnHeaderCheckBoxClick(viewHolder));
                break;
            }
            case ListItem.TYPE_EVENT:{
                ImageItem imageItem = (ImageItem) items.get(position);
                ImageViewHolder viewHolder = (ImageViewHolder) holder;
                viewHolder.dateTime = imageItem.getImage().getDate();
                //item decoration
                if(isLinearLayout){
                    int w = imageItem.getImage().getWidth();
                    int h = imageItem.getImage().getHeight();
                    set.clone((ConstraintLayout) holder.itemView);
                    set.setDimensionRatio(viewHolder.imageView.getId(), w+":"+h);
                    set.applyTo((ConstraintLayout) holder.itemView);
                    listener.linearItemDecoration(viewHolder.imageView);
                }else{
                    viewHolder.imageView.setBackground(null);
                    set.clone((ConstraintLayout) holder.itemView);
                    set.setDimensionRatio(viewHolder.imageView.getId(), "1:1");
                    set.applyTo((ConstraintLayout) holder.itemView);
                }

                Glide.with(context)
                        .load(imageItem.getImage().getImageUri())
                        .fitCenter()
                        .placeholder(R.drawable.image_border)
                        .signature(new ObjectKey(imageItem.getImage().getId()))
                        .into(viewHolder.imageView);
                viewHolder.imageView.setOnClickListener(v -> listener.onItemClick(viewHolder, imageItem));
                viewHolder.imageView.setOnLongClickListener(v -> {
                    listener.OnItemLongClick(viewHolder ,imageItem);
                    setCheckBoxesVisible();
                    return true;
                });

                viewHolder.checkBox.setOnClickListener(v -> listener.OnCheckBoxClick(viewHolder));


                List<CheckBox> checkBoxes = mapCheckboxes.computeIfAbsent(viewHolder.dateTime.truncatedTo(ChronoUnit.DAYS), k -> new ArrayList<>());
                checkBoxes.add(viewHolder.checkBox);

                if(currentState == PhotosFragment.CHANGED_MODE){
                    viewHolder.checkBox.setVisibility(View.VISIBLE);
                }
                if(itemsSelected.contains(viewHolder.getAdapterPosition())){
                    viewHolder.checkBox.setChecked(true);
                }
                break;
            }
            default:
                throw new IllegalStateException("unsupported item view type");
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    public void setCheckBoxesVisible(){
        for(Map.Entry<LocalDateTime, List<CheckBox>> entry: mapCheckboxes.entrySet()){
            for(CheckBox checkBox: entry.getValue()){
                checkBox.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setCheckBoxesInvisible(){
        for(Map.Entry<LocalDateTime, List<CheckBox>> entry: mapCheckboxes.entrySet()){
            for(CheckBox checkBox: entry.getValue()){
                checkBox.setVisibility(View.GONE);
            }
        }
    }

    public void selectAll(){
        for(Map.Entry<LocalDateTime, List<CheckBox>> entry: mapCheckboxes.entrySet()){
            for(CheckBox checkBox: entry.getValue()){
                checkBox.setChecked(true);

            }
        }
    }

    public void unSelectAll(){
        for(Map.Entry<LocalDateTime, List<CheckBox>> entry: mapCheckboxes.entrySet()){
            for(CheckBox checkBox: entry.getValue()){
                checkBox.setChecked(false);
            }
        }
    }

    public void setAllSelectedFlags(boolean allSelectedFlags) {
        this.allSelectedFlags = allSelectedFlags;
    }

    public List<CheckBox> getCheckBoxes(){
        List<CheckBox> checkBoxes = new ArrayList<>();
        for(Map.Entry<LocalDateTime, List<CheckBox>> entry: mapCheckboxes.entrySet()){
            checkBoxes.addAll(entry.getValue());
        }
        return checkBoxes;
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder{
        //TODO

        TextView headerText;
        CheckBox checkBox;
        LocalDateTime dateTime;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerText = itemView.findViewById(R.id.date);
            checkBox = itemView.findViewById(R.id.selected_item);
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        CheckBox checkBox;
        LocalDateTime dateTime;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            checkBox = itemView.findViewById(R.id.selected_item);
        }
    }


}
