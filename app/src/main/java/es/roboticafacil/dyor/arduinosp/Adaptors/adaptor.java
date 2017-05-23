package es.roboticafacil.dyor.arduinosp.Adaptors;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import es.roboticafacil.dyor.arduinosp.Models.Component;
import es.roboticafacil.dyor.arduinosp.R;

//import android.view.LayoutInflater;
//import android.widget.TextView;
//import java.util.ArrayList;

/**
 * Created by Dragos Dunareanu on 02-Apr-17.
 */

public class adaptor extends BaseExpandableListAdapter {

    Context context;
    List<String> groups;
    HashMap<String, List<Component>> comps;

    public adaptor(Context context, List<String> groups, HashMap<String, List<Component>> comps) {
        this.context = context;
        this.groups = groups;
        this.comps = comps;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return comps.get(groups.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.comps.get(this.groups.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.component_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.tv_comp_group_name);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);


        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final Component child = (Component) getChild(groupPosition, childPosition);
        final String childText = child.getName();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.component, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.tv_comp_name);
        textView.setText(childText);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
