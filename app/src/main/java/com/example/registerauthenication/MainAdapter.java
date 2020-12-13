package com.example.registerauthenication;

import android.app.Activity;
import android.app.Dialog;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private List<User> dataList;
    private Activity context;
    String url = "https://5fceddbc9fe7590016e73ef0.mockapi.io/user";

    public MainAdapter(Activity context,List<User> dataList) {
        this.dataList = dataList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User data =dataList.get(position);
        holder.textView.setText(data.getName());
        holder.btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User d= dataList.get(holder.getAdapterPosition());
                DeleteApi(url,d.getId());
                int position = holder.getAdapterPosition();
                dataList.remove(position);
                notifyItemChanged(position);
                notifyItemRangeChanged(position, dataList.size());
            }
        });

        holder.btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User d = dataList.get(holder.getAdapterPosition());
                final String sID = d.getId();
                String sText = d.getName();
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_update);
                int width = WindowManager.LayoutParams.MATCH_PARENT;
                int height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setLayout(width, height);
                dialog.show();
                EditText editText = dialog.findViewById(R.id.edit_text);
                Button btUpdate = dialog.findViewById(R.id.bt_update);
                editText.setText(sText);
                btUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Dismiss dialog
                        dialog.dismiss();
                        String uText = editText.getText().toString().trim();
                        PutApi(url,sID,uText);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView btEdit,btDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_view);
            btEdit = itemView.findViewById(R.id.btnEdit);
            btDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    private void DeleteApi(String url,String id){
        StringRequest stringRequest = new StringRequest(
                Request.Method.DELETE, url + '/' + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, "Successfully", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error by Post data!", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void PutApi(String url,String id, String ten){
        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                url + '/' + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, "Successfully", Toast.LENGTH_SHORT).show();
                dataList.clear();
                getAllUser();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error by Post data!", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()
                    throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("name", ten);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private ArrayList<User> getAllUser(){
        ArrayList<User> list = new ArrayList<>();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, "https://5fceddbc9fe7590016e73ef0.mockapi.io/user", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i =0;i<response.length();i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        String id = object.getString("id");
                        String ten = object.getString("name");
                        User user = new User(id,ten);
                        list.add(user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dataList = list;
                notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonArrayRequest);
        return list;
    }
}
