package com.example.studygo.adapters;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studygo.databinding.ItemContainerRecievedMessageBinding;
import com.example.studygo.databinding.ItemContainerSentMessageBinding;
import com.example.studygo.models.ChatMessage;
import com.example.studygo.utilities.Constants;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECIEVED = 2;
    private final List<ChatMessage> chatMessages;
    private final Bitmap recieverProfileImage;
    private final String senderID;


    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap recieverProfileImage, String senderID) {
        this.chatMessages = chatMessages;
        this.recieverProfileImage = recieverProfileImage;
        this.senderID = senderID;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(ItemContainerSentMessageBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false
            ));
        } else {
            return new RecievedMessageViewHolder(
                    ItemContainerRecievedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        } else {
            ((RecievedMessageViewHolder) holder).setData(chatMessages.get(position), recieverProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).senderID.equals(senderID)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECIEVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
        }
    }

    static class RecievedMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerRecievedMessageBinding binding;

        RecievedMessageViewHolder(ItemContainerRecievedMessageBinding itemContainerRecievedMessageBinding) {
            super(itemContainerRecievedMessageBinding.getRoot());
            binding = itemContainerRecievedMessageBinding;
        }

        void setData(ChatMessage chatMessage, Bitmap recieverProfileImage) {
            if (chatMessage.type != null) {
                Log.d("Sender name", chatMessage.senderName);
                binding.textSenderName.setText(chatMessage.senderName);
            }
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
            binding.imageProfile.setImageBitmap(recieverProfileImage);
        }
    }
}
