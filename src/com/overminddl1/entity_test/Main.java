package com.overminddl1.entity_test;

// As I am lazy and you wanted a demonstration of how to use a component system for something like Minecraft Entities of
// things from a creeper to a cow to an XP Orb and any mixture there-of, here is a completely graphic-less code test.
// This is not an example of prime code, my time is limited so I am limiting the time I spend on this.
// Due to being lazy/out-of-time I will be using the Artemis library for these examples.  Its license allows use even
// for closed source projects with just a simple license disclaimer included somewhere if you decide to use this.
// I do not particularly like the non-dataflow style of Artemis as component systems are supposed to be dataflow based,
// but as java has issues with proper dataflow pattern compared to non-GC languages it seems Artemis is a good
// trade-off, at least for an example such as this, potentially a game as well.
// Artemis could easily be used in MC as well, though if it were I would make a couple of specific changes in its source
// so it would fit in how MC works better, but those changes are fairly trivial to do and would take me about ten
// minutes, I might even do the change in this example since the source of artemis is included.

// I will try to describe what I am doing to try to minimize lack of or misunderstandings.  I will inevitably fail and
// when I do then just ask me to clarify something and I will clarify it in code or comments

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.artemis.utils.ImmutableBag;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        // This is not an MC world obviously, this is a component system world.  These handle things like the
        // registrations of components, entities, etc...
        // The Artemis 'Entity' means a component entity, not a Minecraft style entity.  From here on out I will refer
        // to a Minecraft style entity as a GameObject or something similar on those lines.  'Entity' will refer to an
        // entity of the component system here on.
        // You can easily have multiple worlds, say one of these per each minecraft world/dimension to handle entities
        // (whether GameObjects or a Block type or so), or one each on a chunk to handle block systems, and perhaps a
        // global one to attach a 'renderer system/component' to if you wanted to go in the style of a full component
        // engine so you can more trivially have a completely headless server and a full client in the same system.
        //  I can demonstrate that too if you wish, I can build this example up over time to demonstrate concepts.
        World entityWorld = new World();

        // Some of the stuff done in this example is artemis specific.  Component systems I use in C++ do not require
        // a lot of what is being done here, this is mostly just Java'isms and the usual java verboseness.  I can
        // elaborate more on 'proper component' design later, but from here on out I will try to minimize it as Artemis
        // was designed for component design in games in a Java environment so it focuses on efficiencies for the Java
        // Game environment, sometimes in reducing runtime costs, but also in development style to help make sure that
        // the programmers do not do something too stupid.

        // I will refer to a minecraft 'world' as a dimension from here on out, and an entire savegame as such as well
        // since Artemis uses 'World' to mean its ComponentSystemManager class, easily renamable of course.

        // First thing first before we initialize the World is to register the 'Managers' and 'Systems' of the
        // Entity World.  Of these lets do the Managers first as they are the 'global' parts of the Component system.

        // Managers do not operate with components, rather they manage the state and meta-information of an Entity.
        // Such meta-information could be something as simple as if they are an enemy or what team they are on to
        // more complex ones like a full Oc/KDtree representation for quick area lookups and raytracing.
        // In general anything you use a manager for you can create a component for instead, and in all cases a
        // component could be used fine, the purpose of Managers is to hold the data in a non-component style for fast
        // lookups or other specific things.  This will be demonstrated by the managers below.
        // I will register multiple managers that Artemis includes so as to demonstrate their functionality, but you can
        // easily create your own for more custom functionality.  The ones Artemis includes are just some generic ones
        // that might be useful for some game types as well as to serve as example code.  You do not need to use any of
        // them.

        // The Group Manager is used to 'group' Entities by Strings, so you could specify an Entity is a "MONSTER" or
        // "ANIMAL" or an "Effect" or whatever.  Entities can be in 0 to many groups, and you can look up all the groups
        // that an Entity is in as well as look up all Entities that are in a given group.
        entityWorld.setManager(new GroupManager());

        // The Tag Manager is used for 'naming' a specific Entity a unique name, like tagging an Entity a "BOSS" for a
        // more traditional type game.  This would be used for an Entity type that there could not be more than one of
        // at a time in a game.  This likely would not be used for MC as a map maker or mod could potentially even spawn
        // in multiple Ender Dragons, so this Manager would not make much sense for MC.  In a usual game you would have
        // unique tags like "BOSS" or "PLAYER" if a single player game and so forth.
        entityWorld.setManager(new TagManager());

        // The amount and type of Managers you can have are fairly unbounded, depends on the specific games use-case.

        // Getting in to the Systems are the things that operate on components themselves.  Components are supposed to
        // contain only data, no functionality.  Systems contain functionality, no data.  This separation of concerns is
        // absolutely paramount in component design as it is based on the dataflow pattern.  This has a huge number of
        // useful aspects including but not limited to each component being an 'atomic piece of information' as shown in
        // the example components below, to the data being available to multiple Systems for use at the same time.
        // Artemis does not go as far to make each 'tick' of the world be immutable, but many component systems do go
        // that pure, which allows them to be trivially multi-threadable as systems operate on the components to produce
        // update data for the next tick, which are resolved and coalesced by resolvers at the end of the tick.  Artemis
        // does not go that pure as it more designed for games and ease of use and not raw throughput, instead Artemis
        // is designed so that System that operate on components that are different between them can be fully
        // multi-threaded without issue and ones that are shared can be run serially, though you do the multi-threading
        // yourself based on your game specific characteristics, but because of this it edits the data in-instance,
        // which is more performant for lower efficiency languages like Java and is more usual for the programmers to
        // reason about, thus making it easier to program then real dataflow.

        // A System registers itself to the World saying what Components it wants to know about, what components it
        // 'might' want to know about, and so forth, and quick lookup and access is setup in the backend to make it fast
        // to access that data within the component.  Accessing Components in a System that you do not say that you are
        // interested in is still possible but incurs an extra indirection in the lookup, which is not any kind of
        // significant cost if done rarely, but becomes significant if always done.  This data can also be looked up so
        // you can build a tree on the Systems to know which can be run multi-threaded without issue and which have to
        // be serialized.

        // The basic EntitySystem that you subclass is fairly simple, just containing the mapping data and a tick
        // callback at its most basic, and the ticking is optional.  Artemis provides a few subclasses that you can
        // subclass that provide the basics of useful functionality.  A quick overview of them:
        // VoidEntitySystem:  This one does not provide Entities, just a tick callback, useful for something that needs
        //  to tick but does not access data when it does, like updating an internal clock or something?
        // EntityProcessingSystem:  Provides a helper function that handles the looping over the Entities for you and
        //  just calls a callback method that you are supposed to override where each Entity is passed in to it, easily
        //  optimized by the JVM JIT.  You can still override the main process function but then what is the point of
        //  subclassing this one.
        // DelayedEntityProcessingSystem:  This one subclasses EntityProcessingSystem and by default it does not tick,
        //  but it turns on ticking when a component you are listening on changes state so that you say that it needs to
        //  be called back in <however-long-time>, in which case it will call it at that time then stop again, and it
        //  can listen to many, so you can do something like make a component called TickGrassGrow or whatever and put
        //  it on an Entity when you want to have it tick back at a given time.
        // IntervalEntitySystem:  This subclasses EntitySystem directly and it it like EntityProcessingSystem
        //  except instead of ticking every tick it ticks after however much time you specified in the constructor
        //  elapses each time, so say every half second or so.
        // IntervalEntityProcessingSystem:  This subclasses IntervalEntitySystem and does what EntityProcessingSystem
        //  does to handle the loop for you and you just need to handle a single method callback that gets a passed in
        //  an Entity at a time.

        // An example System I made just to show how it works for generic movement, it uses two components that I made,
        // one is position and the other is physics, see its code to see what/how it works.
        // false == not passive, so it ticks, it can still turn off ticking inside of it like
        // DelayedEntityProcessingSystems do.
        entityWorld.setSystem(new PhysicsSimulationSystem(), false);

        // Lets make a system that randomly jostles around Entities with Physics.
        entityWorld.setSystem(new PhysicsRandomDebugSystem(System.currentTimeMillis()), false);

        // Lets make another example System that eats away at the health of anything that has a Health component every
        // 4/10's second and eventually kills it.
        entityWorld.setSystem(new EverythingWithHealthDiesSystem(), false);

        // It would be useful to make a System that prints the status of all Positionable components, optionally with
        // their physics state, every second or so
        entityWorld.setSystem(new EntityDebugPrinterSystem(), false);

        // Initialize is technically optional, all it does it loop over the managers and systems and call their
        // initialize methods.  You can dynamically add managers and systems if you see a need, just call their
        // initialize method directly if not by here.
        entityWorld.initialize();

        // Lets create an example Entity then
        Entity ball0 = entityWorld.createEntity();
        entityWorld.getManager(GroupManager.class).add(ball0, "BALL");
        entityWorld.getManager(TagManager.class).register("The Ball", ball0);
        ball0.addComponent(new Position());
        ball0.addComponent(new Physics());
        ball0.addToWorld(); // You can add more components after this too, see the Health component below

        // Lets create another example entity
        Entity ball1 = entityWorld.createEntity();
        entityWorld.getManager(GroupManager.class).add(ball1, "BALL");
        ball1.addComponent(new Position(5.0f, 10.0f));
        ball1.addComponent(new Physics(0.0f, 0.0f, 0.5f, 0.0f));
        ball1.addToWorld();

        // Lets create a static entity
        Entity pole = entityWorld.createEntity();
        pole.addComponent(new Position(-5.0f, 8.0f));
        pole.addToWorld();

        // Now lets run the simulation, Ctrl+C it to kill it early, otherwise it will only run for the tick count listed
        // below
        for (int i = 0; i < 600; i++) {
            if (i % 10 == 0) System.out.println(String.format("Currently on tick: %d", i));
            // Artemis uses floats, if used in MC would probably change that to an int or a long to match MC's ticks, or
            // just use the float as seconds and get rid of ticks altogether as really should be done in MC...
            entityWorld.setDelta(1.0f / 20.0f);
            entityWorld.process();
            Thread.sleep(50);

            // After some time, lets add a Health to ball1 and watch it die
            if (i == 100) {
                //ball0.addComponent(new Health(40));
                //ball0.changedInWorld(); // Call this when you add or remove components
                // Or do something like this if you do not have the variable to the entity handy
                Entity ball = entityWorld.getManager(TagManager.class).getEntity("The Ball");
                ball.addComponent(new Health(40));
                ball.changedInWorld();
            }
        }

        ImmutableBag<Entity> entities = entityWorld.getManager(GroupManager.class).getEntities("BALL");
        for(int i=0; i< entities.size(); i++) {
            System.out.println(String.format("Ball %d is still alive at the end with UUID: %s",
                    entities.get(i).getId(), entities.get(i).getUuid()));
        }

    }
}
