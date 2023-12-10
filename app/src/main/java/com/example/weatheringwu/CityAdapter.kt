package com.example.weatheringwu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CityAdapter(private val cities: MutableList<CityInfo>):
    RecyclerView.Adapter<CityAdapter.CityViewHolder>(){
    fun updateData(newData: List<CityInfo>){
        cities.clear();
        cities.addAll(newData);
        notifyDataSetChanged();
    }

    class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val cityNameTextView: TextView = itemView.findViewById(R.id.cityNameTextView)
        val countryTextView: TextView = itemView.findViewById(R.id.countryTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder{
        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_city, parent, false)
        return CityViewHolder(view)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val city = cities[position]
        holder.cityNameTextView.text = city.name
        val location = if (city.state != null) {
            "${city.country}, ${city.state}"
        } else {
            city.country
        }
        holder.countryTextView.text = location
        val coordinates = "Lat: ${city.lat}, Lon: ${city.lon}"
        holder.locationTextView.text = coordinates

        holder.itemView.setOnClickListener{
            listener?.onItemClick(city)
        }
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    interface OnItemClickListener{
        fun onItemClick(cityInfo:CityInfo)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }
}


