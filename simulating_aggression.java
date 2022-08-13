/*This project is inspired by the video "Simulating the Evolution of Aggression" by Primer on YouTube
 * https://www.youtube.com/watch?v=YNMkADpvO4w
 * 
 * An amount of Food is spawned each day (2 pieces of food in each spawn). Creatures will go out to eat the food. If they get only 1 piece of food, they can survive to the next day
 * If they get 2 pieces of food, they will survive and will reproduce, creating an extra creature. Creatures can be Doves or Hawks.
 * 
 * When a Dove encounters another Dove, they will share and each get 1 food. When a Dove encounters a Hawk, the dove will take 0.5 food and the hawk will take 1.5. If two hawks meet, both get 0 food.
 * If a creature eats only 0.5 food they will have a 50% chance of surviving to the next day. If they eat 1.5 they will surely survive and have a 50% chance of reproducing and spawning another creature of the same type.
 * 
 * 
 * In theory the simulation would trend towards 50% Doves-to-Hawks ratio but in practice it can sometimes reach 0% or 100%.
 * This is because probabilities can line up such that all doves or all hawks are eliminated through confrontation and the creature's probability of dying after consuming only 0.5 food.
 * In some cases the number of doves and hawks can exceed the expected maximum population that could be supported by the food supply.
 * Again, this is from probabilities wherein a dove and hawk confront each other, and where the dove does not die but the hawk reproduces.
 */


package simulating_aggression;

import java.util.ArrayList;
import java.util.Random;

public class simulating_aggression {
	public static void main(String[]args) {
		Random rnd = new Random();
		ArrayList<Dove> doves = new ArrayList<Dove>();
		ArrayList<Hawk> hawks = new ArrayList<Hawk>();
		ArrayList<Food> foods = new ArrayList<Food>();
		int num_days = 500;
		int num_doves = 15;
		int num_hawks = 5;
		
		float total_average = 0f;
		
		
		for(int i = 0; i<num_doves; i++) {
			Dove d = new Dove();
			doves.add(d);
		}
		for(int i = 0; i<num_hawks; i++) {
			Hawk h = new Hawk();
			hawks.add(h);
		}
		
		float percentage = (((float)num_doves / ((float)num_doves+(float)num_hawks)) * 100);
		System.out.printf("Doves: %d | Hawks: %d | Total: %d | Dove-Hawk Ratio = %f\n", num_doves, num_hawks, num_doves+num_hawks, percentage);
	
		
		for(int i = 0; i<num_days; i++) {
			num_doves = doves.size();
			num_hawks = hawks.size();
			
			//Delete all the old/uneaten food and replace with new food objects.
			foods.clear();
			for(int f = 0; f<50; f++) { //we spawn a constant amount of food daily. This should shape how large the populations can grow. 20 food should result in a max of 40 doves.
				Food<?, ?> foo = new Food();
				foods.add(foo);
			}
			//Assign creatures to food
			//NOTE: Letting doves go first might not create the most accurate representation of this experiment??
			ArrayList<Integer> foods_indexes = new ArrayList<Integer>(); //create a clone of foods to track the food items which aren't 
			for(int fi = 0; fi<foods.size(); fi++) {
				foods_indexes.add(fi);
			}
			//System.out.println(foods_indexes.toString());
			for(Dove d : doves) {
				int r = d.pick_food(foods_indexes.size());
				//System.out.println("DOVE PICKS "+r);
				if(foods.get(foods_indexes.get(r)).getCreature1Name() == "Null") {
					foods.get(foods_indexes.get(r)).setCreature1(d);
					
				}
				else if(foods.get(foods_indexes.get(r)).getCreature1Name() != "Null" && foods.get(foods_indexes.get(r)).getCreature2Name() == "Null") {
					foods.get(foods_indexes.get(r)).setCreature2(d);
					foods_indexes.remove(r); //this piece of food is full, we can remove it from the clone list
				}
				else {
					System.out.println("Overwrite");
					System.exit(0);
				}
				//System.out.println(foods_indexes.toString());
			}

			
			for(Hawk h : hawks) {
				//System.out.println("Hawks: " +foods_indexes.toString());
				int r;
				try { //There are circumstances where you may end up with more creatures than food could sustain, and this comes from probability, particularly in cases where a dove and hawk meet where the dove survives and the hawk reproduces
					r = h.pick_food(foods_indexes.size());
				}
				catch(Exception e) {
					break;
				}
				
				//System.out.println("HAWK PICKS "+r);
				if(foods.get(foods_indexes.get(r)).getCreature1Name() == "Null") {
					foods.get(foods_indexes.get(r)).setCreature1(h);
				}
				else if(foods.get(foods_indexes.get(r)).getCreature1Name() != "Null" && foods.get(foods_indexes.get(r)).getCreature2Name() == "Null") {
					foods.get(foods_indexes.get(r)).setCreature2(h);
					foods_indexes.remove(r); //this piece of food is full, we can remove it from the clone list
				}
				else {
					System.out.println("Overwrite");
					System.exit(0);
				}
				
			}
			//System.out.println(foods_indexes.toString());
			
			//Have doves and hawks consume their food
			for(Food<?, ?> f: foods) {
				//System.out.println("Creature 1: "+f.getCreature1Name()+" Creature 2: "+f.getCreature2Name());
				if(f.getCreature1Name() == "simulating_aggression.Hawk" && f.getCreature2Name() == "Null") {
					((Hawk) f.getCreature1()).consume_food(2.0f);
				}
				else if(f.getCreature1Name() == "simulating_aggression.Dove" && f.getCreature2Name() == "Null") {
					((Dove) f.getCreature1()).consume_food(2.0f);
				}
				else if(f.getCreature1Name() == "simulating_aggression.Dove" && f.getCreature2Name() == "simulating_aggression.Dove") {
					((Dove) f.getCreature1()).consume_food(1.0f);
					((Dove) f.getCreature2()).consume_food(1.0f);
				}
				else if(f.getCreature1Name() == "simulating_aggression.Dove" && f.getCreature2Name() == "simulating_aggression.Hawk") {
					((Dove) f.getCreature1()).consume_food(0.5f);
					((Hawk) f.getCreature2()).consume_food(1.5f);
				}
				else if(f.getCreature1Name() == "simulating_aggression.Hawk" && f.getCreature2Name() == "simulating_aggression.Dove") {
					((Hawk) f.getCreature1()).consume_food(1.5f);
					((Dove) f.getCreature2()).consume_food(0.5f);
				}
			}
			
			
			//Each dove and hawk will attempt to reproduce if they ate enough food, or nothing happens, or they die
			int doves_to_add = 0;
			int doves_to_remove = 0;
			for(Dove d : doves) {
				if(d.getFood_consumed() == 2.0f) {
					doves_to_add++;
				}
				else if(d.getFood_consumed() == 1.5f) {
					if(rnd.nextBoolean()) { //This is not exactly 50% chance but close
						doves_to_add++;
					}
				}
				else if(d.getFood_consumed() == 0.5f) {
					if(rnd.nextBoolean()) {
						doves_to_remove++;
					}
				}
				else if(d.getFood_consumed() == 0.0f){
					doves_to_remove++;
				}
				d.setFood_consumed(0.0f); //reset food consumed
			}
			for(int a = 0; a<doves_to_add; a++) {
				Dove d = new Dove();
				doves.add(d);
			}
			for(int a = 0; a<doves_to_remove; a++) {
				doves.remove(0);
			}
			
			
		
			int hawks_to_add = 0;
			int hawks_to_remove = 0;
			for(Hawk h: hawks) {
				if(h.getFood_consumed() == 2.0f) {
					hawks_to_add++;
				}
				else if(h.getFood_consumed() == 1.5f) {
					if(rnd.nextBoolean()) { //This is not exactly 50% chance but close
						hawks_to_add++;
					}
				}
				else {
					hawks_to_remove++;
				}
				h.setFood_consumed(0.0f);
			}
			for(int a = 0; a<hawks_to_add; a++) {
				Hawk h = new Hawk();
				hawks.add(h);
			}
			for(int a = 0; a<hawks_to_remove; a++) {
				hawks.remove(0);
			}
			
			num_doves = doves.size();
			num_hawks = hawks.size();
			//Output data
			percentage = (((float)num_doves / ((float)num_doves+(float)num_hawks)) * 100);
			System.out.printf("Doves: %d | Hawks: %d | Total: %d | Dove-Hawk Ratio = %f\n", num_doves, num_hawks, num_doves+num_hawks, percentage);
			total_average += percentage;
		}
		
		System.out.println("Total average is "+total_average/num_days);
	}
}

class Dove{
	private float food_consumed;
	
	public Dove() {
		food_consumed = 0.0f;
	}
	
	public float getFood_consumed() {
		return food_consumed;
	}
	
	public void setFood_consumed(float f) {
		food_consumed = f;
	}
	
	public void consume_food(float f) {
		food_consumed += f;
	}
	
	public int pick_food(int arraysize) {
		Random rnd = new Random();
		int index = rnd.nextInt(arraysize);
		return index;
	}
}

class Hawk{
	private float food_consumed;
	
	public Hawk() {
		food_consumed = 0.0f;
	}
	
	public float getFood_consumed() {
		return food_consumed;
	}
	
	public void setFood_consumed(float f) {
		food_consumed = f;
	}
	
	public void consume_food(float f) {
		food_consumed += f;
	}
	
	public int pick_food(int arraysize) {
		Random rnd = new Random();
		int index = rnd.nextInt(arraysize);
		return index;
	}
}

class Food<A, B>{ //Java allows use of generic object types to be later provided. Fantastic!
	A creature1;
	B creature2;
	
	public Object getCreature1(){
		if(creature1.getClass() == null) {
			return "Null";
		}
		else if(creature1.getClass().getName() == "simulating_aggression.Hawk"){
			return (Hawk) creature1;
		}
		else {
			return (Dove) creature1;
		}
	}
	public Object getCreature2(){
		if(creature2.getClass() == null) {
			return "Null";
		}
		else if(creature2.getClass().getName() == "simulating_aggression.Hawk"){
			return (Hawk) creature2;
		}
		else {
			return (Dove) creature2;
		}
	}
	
	public String getCreature1Name(){
		if(creature1 == null) {
			return "Null";
		}
		else {
			Class<? extends Object> a = creature1.getClass();
			return a.getName();
		}
		
	}
	public String getCreature2Name(){
		if(creature2 == null) {
			return "Null";
		}
		else {
			Class<? extends Object> a = creature2.getClass();
			return a.getName();
		}
	}
	
	public void setCreature1(A o) {
		creature1 = o;
	}
	public void setCreature2(B o) {
		creature2 = o;
	}
}
