


//Java核心系统
function CreatorsPackage(name) {
	var p = Packages.rhino.NativeJavaPackage(name, Vars.mods.mainLoader());
	Packages.rhino.ScriptRuntime.setObjectProtoAndParent(p, Vars.mods.scripts.scope)
	return p
}
var CreatorsJavaPack = CreatorsPackage('CtCoreSystem')
importPackage(CreatorsJavaPack)
importPackage(CreatorsJavaPack.CoreSystem)
importPackage(CreatorsJavaPack.CoreSystem.dialogs)
importPackage(CreatorsJavaPack.CoreSystem.type)
importPackage(CreatorsJavaPack.content)
importPackage(CreatorsJavaPack.ui)
importPackage(CreatorsJavaPack.CoreSystem.type.VXV)
importPackage(CreatorsJavaPack.ui.dialogs)
importPackage(CreatorsJavaPack.CoreSystem.type.Ovulam5480)



CreatorsModJS.RunName.add("ctcoresystem")
CreatorsModJS.DawnRun.add(run(() => { }));


//require('gong_neng');
if ((Vars.mods.locateMod("creators") != null)) {
	require('biansu2');
}{};
	//require("CoreItems")
//require('nihility');//虚无护盾 已经有另一个虚无了 java
Blocks.worldMessage.maxTextLength = 9527520;



// if(Version.__javaObject__.getFields() == 6){
// 	require("CoreItems")
// }

/*if(Version.__javaObject__.fields.length == 6){//检测到是其他端时不加载核心资源显示
	require("CoreItems")
}//V8判断不到原版的字段*/








/*let mod = Vars.mods.getMod("蓝图效率");
if (mod == null) {
	let bd = Vars.mods.locateMod("ctcoresystem");
	let fi = bd.root.child("mod")
		.child("显示蓝图消耗产出效率[v1.2].zip");
	Vars.mods.importMod(fi);
	Vars.mods.locateMod("蓝图效率");
};*/

/*
let mod2 = Vars.mods.getMod("auto_saver");
if (mod2 == null) {
	let bd = Vars.mods.locateMod("ctcoresystem");
	let fi = bd.root.child("mod")
		.child("AutoSaver.jar");
	Vars.mods.importMod(fi);
	Vars.mods.locateMod("auto_saver");
};
*/

/* let mod = Vars.mods.locateMod("auto_saver");
 if (mod == null) {
	 let bd = Vars.mods.locateMod("ctcoresystem");
	 let fi = bd.root.child("mod")
		 .child("AutoSaver.jar");
	 Vars.mods.importMod(fi);
 };*/


//Vars.mods.getMod("蓝图效率").state = Mods.ModState.disabled;

/*
//检测学术端和X端
try {
	//java.lang.Class.forName("mindustry.arcModule.ARCVars");
	java.lang.Class.forName("mindustryX.VarsX");
} catch (e if java.lang.ClassNotFoundException) {

	try {
		java.lang.Class.forName("mindustry.arcModule.ARCVars");
		//java.lang. Class.forName("mindustryX.VarsX");
	} catch (e if java.lang.ClassNotFoundException) {

	}
} */