package app.nehc.batterytool.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.nehc.batterytool.BatteryStats;
import app.nehc.batterytool.R;
import app.nehc.batterytool.bean.FuncItem;
import app.nehc.batterytool.service.MonitoringService;
import app.nehc.batterytool.utils.ConfigUtil;

public class FunctionListAdapter extends RecyclerView.Adapter<FunctionListAdapter.InnerViewHolder> {

    private List<FuncItem> mList;
    private Context context;

    public FunctionListAdapter(Context context, List<FuncItem> mList) {
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public InnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_func_list, parent, false);
        InnerViewHolder viewHolder = new InnerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull InnerViewHolder holder, int position) {
        holder.funcName.setText(mList.get(position).getFuncName());
        if (mList.get(position).isSwitchItem()) {
            //设置可见性
            holder.funcSwitch.setVisibility(View.VISIBLE);
            //将设置保存到文件
            holder.funcSwitch.setChecked(mList.get(position).isEnable());
            holder.funcSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                ConfigUtil.setPackageName(context.getPackageName());
                ConfigUtil.writeSetting(mList.get(position).getFuncId(), isChecked);
            });

        }
        //根据id进一步细分事件
        holder.itemView.setOnClickListener((v) -> {
            FuncItem item = mList.get(position);
            //细分事件
            subdivideEvents(holder, item.getFuncId());
            //切换开关视图
            if (item.isSwitchItem()) {
                holder.funcSwitch.setChecked(!item.isEnable());
                item.setEnable(!item.isEnable());
            }
        });
    }

    private void subdivideEvents(InnerViewHolder holder, int id) {
        switch (id) {
            case 10000://充电提醒
                //判断电池白名单，并加入
                PowerManager powerManager = (PowerManager) context.getSystemService(context.POWER_SERVICE);
                boolean isIgnored = powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
                if (!isIgnored && !holder.funcSwitch.isChecked()) {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                    context.startActivity(intent);
                }
                //
                if (!holder.funcSwitch.isChecked()) {
                    context.startService(new Intent(context, MonitoringService.class));
                    Toast.makeText(context, "启用充电提醒", Toast.LENGTH_SHORT).show();
                    if (!powerManager.isIgnoringBatteryOptimizations(context.getPackageName())) {
                        Toast.makeText(context, "注意：未忽略电池优化将会影响此功能", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    context.stopService(new Intent(context, MonitoringService.class));
                }
                break;
            case 10001:
                context.startActivity(new Intent(context, BatteryStats.class));
                break;
            case 10002:
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$PowerUsageSummaryActivity"));
                context.startActivity(intent);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class InnerViewHolder extends RecyclerView.ViewHolder {
        private TextView funcName;
        private Switch funcSwitch;

        public InnerViewHolder(@NonNull View itemView) {
            super(itemView);
            funcName = itemView.findViewById(R.id.funcName);
            funcSwitch = itemView.findViewById(R.id.funcSwitch);
        }
    }
}
