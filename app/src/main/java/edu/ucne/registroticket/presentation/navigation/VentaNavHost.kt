package edu.ucne.registroticket.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import edu.ucne.registroticket.presentation.ventas.VentaListScreen
import edu.ucne.registroticket.presentation.ventas.VentasScreen

@Composable
fun VentaNavHost(
    navHostController: NavHostController
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.VentaList
    ) {
        composable <Screen.VentaList> {
            VentaListScreen (
                goToVenta = {
                    navHostController.navigate(Screen.Venta(it))
                },
                createVenta = {
                    navHostController.navigate(Screen.Venta(0))
                }
            )
        }

        composable<Screen.Venta> {
            val args = it.toRoute<Screen.Venta>()
            VentasScreen (
                VentaId = args.ventaId,
                goBack = {
                    navHostController.navigateUp()
                }
            )
        }
    }
}