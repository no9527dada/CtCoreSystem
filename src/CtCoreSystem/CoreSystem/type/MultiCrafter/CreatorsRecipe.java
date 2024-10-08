package CtCoreSystem.CoreSystem.type.MultiCrafter;

import mindustry.type.*;

import static mindustry.type.ItemStack.*;

//多合成前置 1/2
public class CreatorsRecipe {
    public final InputContents input;
    public final OutputContents output;
    public final float craftTime;

    public CreatorsRecipe(InputContents input, OutputContents output, float craftTime) {
        this.input = input;
        this.output = output;
        this.craftTime = craftTime;
    }

    public static class InputContents {
        public final ItemStack[] items;
        public final LiquidStack[] liquids;
        public final float power;

        public InputContents(ItemStack[] items, LiquidStack[] liquids, float power) {
            this.items = items;
            this.liquids = liquids;
            this.power = power;
        }

        public InputContents(ItemStack[] items, float power) {
            this(items, new LiquidStack[]{}, power);
        }

        public InputContents(LiquidStack[] liquids, float power) {
            this(with(), liquids, power);
        }

        public InputContents(ItemStack[] items, LiquidStack[] liquids) {
            this(items, liquids, 0f);
        }

        public InputContents(ItemStack[] items) {
            this(items, 0f);
        }

        public InputContents(LiquidStack[] liquids) {
            this(liquids, 0f);
        }

        public InputContents(float power) {
            this(with(), power);
        }

        public InputContents() {
            this(0f);
        }
    }

    public static class OutputContents {
        public final ItemStack[] items;
        public final LiquidStack[] liquids;
        public final float power;

        public OutputContents(ItemStack[] items, LiquidStack[] liquids, float power) {
            this.items = items;
            this.liquids = liquids;
            this.power = power;
        }

        public OutputContents(ItemStack[] items, float power) {
            this(items, new LiquidStack[]{}, power);
        }

        public OutputContents(LiquidStack[] liquids, float power) {
            this(with(), liquids, power);
        }

        public OutputContents(ItemStack[] items, LiquidStack[] liquids) {
            this(items, liquids, 0f);
        }

        public OutputContents(ItemStack[] items) {
            this(items, 0f);
        }

        public OutputContents(LiquidStack[] liquids) {
            this(liquids, 0f);
        }

        public OutputContents(float power) {
            this(with(), power);
        }

        public OutputContents() {
            this(0f);
        }
    }
}