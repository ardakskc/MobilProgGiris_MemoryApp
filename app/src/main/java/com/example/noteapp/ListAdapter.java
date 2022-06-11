package com.example.noteapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Notes> list;
    private Database db;
    private String pass;

    public ListAdapter(Context context, ArrayList<Notes> list){
        this.context=context;
        this.list=list;
        db = new Database(context);
    }

    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View listItem = layoutInflater.inflate(R.layout.note_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }


    public void onBindViewHolder(ViewHolder holder, int position) {

        Notes note = list.get(position);
        holder.baslik.setText(note.getBaslik());
        holder.date.setText(note.getDate());
        holder.kilit.setImageResource(R.drawable.ic_lock_open);
        holder.lokasyon.setText(note.getLokasyon());
        holder.foto.setImageResource(R.drawable.em_note);
        holder.share.setImageResource(R.drawable.ic_share);
        if (note.getMood() == 2) {
            holder.em.setImageResource(R.drawable.ic_baseline_sentiment_very_dissatisfied_24);
        } else {
            holder.em.setImageResource(R.drawable.ic_baseline_sentiment_very_satisfied_24);
        }
        holder.del.setImageResource(R.drawable.ic_baseline_delete_24);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Notes temp = list.get(holder.getAdapterPosition());
                if (temp.getSifre().equals("NULL")){
                    Intent intent = new Intent(view.getContext(), Write_Note.class);
                    intent.putExtra("Not", temp);
                    try {
                        view.getContext().startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(view.getContext(), "Error in card view.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(view.getContext(), "Memory Locked.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Notes temp = list.get(holder.getAdapterPosition());
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                builder.setTitle("Are you sure you want to delete this item?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteNote(temp);
                        list.remove(temp);
                        notifyItemRemoved(holder.getAdapterPosition());
                        Toast.makeText(view.getContext(), "Memory Deleted.", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(view.getContext(), "Nothing Deleted.", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
                builder.show();
               /*db.deleteNote(temp);
                list.remove(temp);
                notifyItemRemoved(holder.getAdapterPosition());
                Toast.makeText(view.getContext(), "Note Deleted.", Toast.LENGTH_SHORT).show();*/
            }
        });
        holder.kilit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Notes temp = list.get(holder.getAdapterPosition());
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                if(temp.getSifre().equals("NULL")){
                    builder.setTitle("Add Password");

                    final EditText input = new EditText(view.getRootView().getContext());
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setView(input);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pass = input.getText().toString();
                            temp.setSifre(pass);
                            db.updateNote(temp);
                            Toast.makeText(view.getContext(), "Memory Locked.", Toast.LENGTH_SHORT).show();
                            holder.kilit.setImageResource(R.drawable.ic_lock);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }else {
                    builder.setTitle("Enter the Password");
                    final EditText input = new EditText(view.getRootView().getContext());
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setView(input);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pass = input.getText().toString();
                            if (temp.getSifre().equals(pass)){
                                temp.setSifre("NULL");
                                db.updateNote(temp);
                                Toast.makeText(view.getContext(), "Memory Unlocked.", Toast.LENGTH_SHORT).show();
                                holder.kilit.setImageResource(R.drawable.ic_lock_open);
                            }else {
                                Toast.makeText(view.getContext(), "Wrong Password.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }

            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Notes temp = list.get(holder.getAdapterPosition());

                if (temp.getSifre().equals("NULL")){
                    StringBuilder sb = new StringBuilder();
                    sb.append("Title: "+temp.getBaslik()+" \n");
                    sb.append("Date: "+temp.getDate()+" \n");
                    sb.append("Location: "+temp.getLokasyon()+" \n");
                    sb.append("---------------------------------\n");
                    sb.append(temp.getText()+" \n");
                    String mesaj = sb.toString();
                    //Intent smsIntent = new Intent(Intent.ACTION_SEND);
                    //smsIntent.setData(Uri.parse("smsto:"));
                    //smsIntent.putExtra("address"  , new String (""));
                    //smsIntent.putExtra("sms_body"  , mesaj);

                    Intent smsIntent = new Intent(Intent.ACTION_SEND);
                    smsIntent.setType("text/plain");
                    smsIntent.putExtra(Intent.EXTRA_TEXT, mesaj);

                    Intent chooserIntent = Intent.createChooser(smsIntent, "Share memory with:");
                    chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    try {
                        context.startActivity(chooserIntent);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Toast.makeText(view.getContext(), "SMS failed.", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(view.getContext(), "Memory Locked.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView foto,em;
        public TextView baslik,date,lokasyon;
        public ImageButton del,kilit,share;

        public ViewHolder(View itemView) {
            super(itemView);
            foto = (ImageView) itemView.findViewById(R.id.note_img);
            baslik = (TextView) itemView.findViewById(R.id.baslik);
            date = (TextView) itemView.findViewById(R.id.date_text);
            lokasyon = (TextView) itemView.findViewById(R.id.lokasyon);
            kilit = (ImageButton) itemView.findViewById(R.id.kilit_but);
            em = (ImageView) itemView.findViewById(R.id.emoji);
            del = (ImageButton) itemView.findViewById(R.id.delete);
            share = (ImageButton) itemView.findViewById(R.id.share_but);

        }

    }
}
